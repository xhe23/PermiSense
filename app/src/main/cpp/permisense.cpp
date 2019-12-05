//
// Created by swordfeng on 12/4/19.
//

#include <xposed_permission_settings.h>
#include <cstdlib>
#include <cstring>
#include <android/log.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <sys/select.h>
#include <cerrno>
#include <thread>
#include <atomic>
#include <memory>
#include <vector>
#include <unistd.h>
#include <string>
#include <unordered_map>
#include <shared_mutex>
#include <unordered_set>

using std::atomic_bool;
using std::unique_ptr;
using std::vector;
using std::string;
using std::unordered_map;
using std::shared_timed_mutex;
using std::unique_lock;
using std::shared_lock;
using std::unordered_set;

char* SOCK_NAME = nullptr;
std::thread serverThread;
atomic_bool killing(false);
shared_timed_mutex permMutex;
unordered_map<string, unordered_set<char>> revokedPermissions;
unordered_map<string, int> socketsTracker;

constexpr int TYPE_QUERY_PERMISSION = 0;
constexpr int TYPE_INV = 255;

void removeSocket(string sockname) {
    auto it = socketsTracker.find(sockname);
    if (it != socketsTracker.end()) {
        shutdown(it->second, SHUT_RDWR);
        close(it->second);
    }
    socketsTracker.erase(it);
}

void cleanSocket() {
    for (auto& p: socketsTracker) {
        shutdown(p.second, SHUT_RDWR);
        close(p.second);
    }
    socketsTracker.clear();
}

void createSocket(string sockname) {
    auto it = socketsTracker.find(sockname);
    if (it != socketsTracker.end()) {
        return;
    }

    struct sockaddr_un addr;
    memset(&addr, 0, sizeof(addr));
    addr.sun_family = AF_UNIX;
    addr.sun_path[0] = '\0';
    strncpy(&addr.sun_path[1], SOCK_NAME, sizeof(addr.sun_path) - 1);
    strncpy(&addr.sun_path[16], sockname.c_str(), sizeof(addr.sun_path) - 16);

    int sockfd = socket(AF_UNIX, SOCK_SEQPACKET | SOCK_CLOEXEC, 0);
    if (sockfd < 0) {
        __android_log_print(ANDROID_LOG_ERROR, "PermiSense", "socket: %s", strerror(errno));
        return;
    }
    if (bind(sockfd, (struct sockaddr*)&addr, sizeof(addr)) != 0) {
        __android_log_print(ANDROID_LOG_ERROR, "PermiSense", "bind: %s", strerror(errno));
        close(sockfd);
        return;
    }
    if (listen(sockfd, 4) != 0) {
        __android_log_print(ANDROID_LOG_ERROR, "PermiSense", "listen: %s", strerror(errno));
        close(sockfd);
        return;
    }
    socketsTracker[sockname] = sockfd;
}

void grantPermission(const string& packageName, const string& permissions) {
    unique_lock<shared_timed_mutex> lock(permMutex);
    auto it = revokedPermissions.find(packageName);
    if (it == revokedPermissions.end()) return;
    for (char perm: permissions) {
        it->second.erase(perm);


        string sockname;
        sockname.push_back(perm);
        sockname += packageName;
        removeSocket(sockname);
    }
}

void revokePermission(const string& packageName, const string& permissions) {
    __android_log_print(ANDROID_LOG_INFO, "PermiSense", "revoke %s %s", packageName.c_str(), permissions.c_str());
    unique_lock<shared_timed_mutex> lock(permMutex);
    __android_log_print(ANDROID_LOG_INFO, "PermiSense", "lock");
    auto it = revokedPermissions.find(packageName);
    if (it == revokedPermissions.end()) {
        revokedPermissions.insert({packageName, unordered_set<char>{}});
        it = revokedPermissions.find(packageName);
    }
    __android_log_print(ANDROID_LOG_INFO, "PermiSense", "foreach");
    for (char perm: permissions) {
        __android_log_print(ANDROID_LOG_INFO, "PermiSense", "perm: %u", static_cast<uint8_t>(perm));
        it->second.insert(perm);

        __android_log_print(ANDROID_LOG_INFO, "PermiSense", "createsock");

        string sockname;
        sockname.push_back(perm);
        sockname += packageName;
        createSocket(sockname);
    }
}

bool queryPermission(const string& packageName, char permission) {
    shared_lock<shared_timed_mutex> lock(permMutex);
    auto it = revokedPermissions.find(packageName);
    if (it == revokedPermissions.end()) return true;
    return it->second.find(permission) == it->second.end();
}

void handleRequest(vector<uint8_t> &buf) {
    if (buf.empty()) return;
    int type = buf[0];
    if (type == TYPE_QUERY_PERMISSION) {
        char permission = buf[1];
        size_t pnlen = *reinterpret_cast<uint32_t*>(&buf[2]);
        string packageName(reinterpret_cast<char*>(buf.data() + 6), pnlen);
        bool result = queryPermission(packageName, permission);
        buf.resize(1);
        buf[0] = static_cast<uint8_t>(result);
    } else {
        buf.clear();
    }
}

void server(int listenfd) {
    struct sockaddr_un in_addr;
    socklen_t addrlen = sizeof(in_addr);
    vector<uint8_t> buf;
    while (!killing) {
        int sockfd = accept4(listenfd, (struct sockaddr *)&in_addr, &addrlen, SOCK_CLOEXEC);
        if (sockfd < 0) {
            __android_log_print(ANDROID_LOG_ERROR, "PermiSense", "accept: %s", strerror(errno));
            break;
        }
        fd_set fds;
        FD_ZERO(&fds);
        FD_SET(sockfd, &fds);
        struct timespec timeout;
        timeout.tv_sec = 0;
        timeout.tv_nsec = 10L * 1000L * 1000L; // 10 ms
        int ready = pselect(1, &fds, nullptr, nullptr, &timeout, nullptr);
        if (ready == -1) {
            __android_log_print(ANDROID_LOG_ERROR, "PermiSense", "select: %s", strerror(errno));
        }
        if (FD_ISSET(sockfd, &fds)) {
            // handle message
            buf.resize(1024);
            ssize_t len = recv(sockfd, buf.data(), buf.size(), 0);
            if (len < 0) {
                __android_log_print(ANDROID_LOG_ERROR, "PermiSense", "recv: %s", strerror(errno));
            } else if (len == 0) {
                // eof
            } else {
                buf.resize((size_t) len);
                handleRequest(buf);
                if (buf.size() > 0) {
                    send(sockfd, buf.data(), buf.size(), 0);
                }
            }
        }
        shutdown(sockfd, SHUT_RDWR);
        close(sockfd);
    }
    killing.store(false);
}

void startServer() {
    if (serverThread.joinable()) return;
    int sockfd = socket(AF_UNIX, SOCK_SEQPACKET | SOCK_CLOEXEC, 0);
    if (sockfd < 0) {
        __android_log_print(ANDROID_LOG_ERROR, "PermiSense", "socket: %s", strerror(errno));
        return;
    }

    struct sockaddr_un addr;
    memset(&addr, 0, sizeof(addr));
    addr.sun_family = AF_UNIX;
    addr.sun_path[0] = '\0';
    strncpy(&addr.sun_path[1], SOCK_NAME, sizeof(addr.sun_path) - 1);
    if (bind(sockfd, (struct sockaddr*)&addr, sizeof(addr)) != 0) {
        __android_log_print(ANDROID_LOG_ERROR, "PermiSense", "bind: %s", strerror(errno));
        close(sockfd);
        return;
    }
    if (listen(sockfd, 16) != 0) {
        __android_log_print(ANDROID_LOG_ERROR, "PermiSense", "listen: %s", strerror(errno));
        close(sockfd);
        return;
    }
    serverThread = std::thread(server, sockfd);
}

bool makeRequest(vector<uint8_t>& buf) {
    struct sockaddr_un addr;
    memset(&addr, 0, sizeof(addr));
    addr.sun_family = AF_UNIX;
    addr.sun_path[0] = '\0';
    strncpy(&addr.sun_path[1], SOCK_NAME, sizeof(addr.sun_path) - 1);
    int sockfd = socket(AF_UNIX, SOCK_SEQPACKET | SOCK_CLOEXEC, 0);
    if (sockfd < 0) {
        __android_log_print(ANDROID_LOG_ERROR, "PermiSense", "socket: %s", strerror(errno));
        return false;
    }
    if (connect(sockfd, (struct sockaddr*)&addr, sizeof(addr)) != 0) {
        __android_log_print(ANDROID_LOG_ERROR, "PermiSense", "connect: %s", strerror(errno));
        close(sockfd);
        return false;
    }
    if (send(sockfd, buf.data(), buf.size(), 0) < 0) {
        __android_log_print(ANDROID_LOG_ERROR, "PermiSense", "send: %s", strerror(errno));
        shutdown(sockfd, SHUT_RDWR);
        close(sockfd);
        return false;
    }
    buf.resize(1024);
    ssize_t len = recv(sockfd, buf.data(), buf.size(), 0);
    if (len <= 0) {
        __android_log_print(ANDROID_LOG_ERROR, "PermiSense", "recv: %s", strerror(errno));
        shutdown(sockfd, SHUT_RDWR);
        close(sockfd);
        return false;
    }
    buf.resize(len);
    return true;
}

int alternativeQuery(const string &packageName, char permission) {
    string sockname;
    sockname.push_back(permission);
    sockname += packageName;

    struct sockaddr_un addr;
    memset(&addr, 0, sizeof(addr));
    addr.sun_family = AF_UNIX;
    addr.sun_path[0] = '\0';
    strncpy(&addr.sun_path[1], SOCK_NAME, sizeof(addr.sun_path) - 1);

    int sockfd = socket(AF_UNIX, SOCK_SEQPACKET | SOCK_CLOEXEC, 0);
    if (sockfd < 0) {
        return -1;
    }
    if (connect(sockfd, (struct sockaddr*)&addr, sizeof(addr)) != -1 || errno != EACCES) {
        close(sockfd);
        return -1;
    }
    shutdown(sockfd, SHUT_RDWR);
    close(sockfd);

    __android_log_print(ANDROID_LOG_ERROR, "PermiSense", "running alternative query");

    memset(&addr, 0, sizeof(addr));
    addr.sun_family = AF_UNIX;
    addr.sun_path[0] = '\0';
    strncpy(&addr.sun_path[1], SOCK_NAME, sizeof(addr.sun_path) - 1);
    strncpy(&addr.sun_path[16], sockname.c_str(), sizeof(addr.sun_path) - 16);

    sockfd = socket(AF_UNIX, SOCK_SEQPACKET | SOCK_CLOEXEC, 0);
    if (sockfd < 0) {
        __android_log_print(ANDROID_LOG_ERROR, "PermiSense", "socket: %s", strerror(errno));
        return -1;
    }
    int res;
    if (connect(sockfd, (struct sockaddr*)&addr, sizeof(addr)) == 0 || errno == EACCES) {
        res = 0;
    } else {
        res = 1;
    }
    shutdown(sockfd, SHUT_RDWR);
    close(sockfd);
    return res;
}

int queryPermissionRequest(const string &packageName, char permission) {
    vector<uint8_t> buf;
    buf.reserve(1024);
    buf.resize(6);
    buf[0] = TYPE_QUERY_PERMISSION;
    buf[1] = (uint8_t) permission;
    *reinterpret_cast<uint32_t*>(&buf[2]) = packageName.size();
    for (char c: packageName) {
        buf.push_back((uint8_t) c);
    }
    if (!makeRequest(buf)) {
//        return -1;
        return alternativeQuery(packageName, permission);
    }
    return buf[0];
}

void stopServer() {
    if (!serverThread.joinable()) return;
    killing.store(true);
    vector<uint8_t> buf;
    buf.push_back(TYPE_INV);
    makeRequest(buf);
    serverThread.join();
    cleanSocket();
}

extern "C" {
JNIEXPORT void JNICALL
Java_com_installedapps_com_installedapps_xposed_XposedPermissionSettings_grantPermission
        (JNIEnv *env, jclass clazz, jstring jpackageName, jstring jpermissions) {
    jsize len;
    const char* chars;

    len = env->GetStringUTFLength(jpackageName);
    chars = env->GetStringUTFChars(jpackageName, nullptr);
    string packageName(chars, len);
    env->ReleaseStringUTFChars(jpackageName, chars);

    len = env->GetStringUTFLength(jpermissions);
    chars = env->GetStringUTFChars(jpermissions, nullptr);
    string permissions(chars, len);

    grantPermission(packageName, permissions);
}


JNIEXPORT void JNICALL
Java_com_installedapps_com_installedapps_xposed_XposedPermissionSettings_revokePermission
        (JNIEnv *env, jclass clazz, jstring jpackageName, jstring jpermissions) {
    jsize len;
    const char* chars;

    len = env->GetStringUTFLength(jpackageName);
    chars = env->GetStringUTFChars(jpackageName, nullptr);
    string packageName(chars, len);
    env->ReleaseStringUTFChars(jpackageName, chars);

    len = env->GetStringUTFLength(jpermissions);
    chars = env->GetStringUTFChars(jpermissions, nullptr);
    string permissions(chars, len);

    revokePermission(packageName, permissions);
}


JNIEXPORT jint JNICALL
Java_com_installedapps_com_installedapps_xposed_XposedPermissionSettings_queryPermission
        (JNIEnv *env, jclass, jstring jpackageName, jchar permission) {
    jsize len = env->GetStringUTFLength(jpackageName);
    const char* chars = env->GetStringUTFChars(jpackageName, nullptr);
    string packageName(chars, len);
    env->ReleaseStringUTFChars(jpackageName, chars);
    return static_cast<jint>(queryPermissionRequest(packageName, (char) permission));
}

JNIEXPORT void JNICALL Java_com_installedapps_com_installedapps_xposed_XposedPermissionSettings_runServer
        (JNIEnv *env, jclass clazz) {
    __android_log_print(ANDROID_LOG_INFO, "PermiSense", "native server started");
    startServer();
}

JNIEXPORT void JNICALL Java_com_installedapps_com_installedapps_xposed_XposedPermissionSettings_killServer
        (JNIEnv *, jclass) {
    stopServer();
}

JNIEXPORT void JNICALL Java_com_installedapps_com_installedapps_xposed_XposedPermissionSettings_init
        (JNIEnv *env, jclass, jstring socketName) {
    jsize len = env->GetStringUTFLength(socketName);
    const char* chars = env->GetStringUTFChars(socketName, nullptr);
    SOCK_NAME = new char[len + 1];
    strncpy(SOCK_NAME, chars, len);
    SOCK_NAME[len] = '\0';
    env->ReleaseStringUTFChars(socketName, chars);
}

}