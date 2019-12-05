package com.installedapps.com.installedapps.xposed;

public class XposedPermissionSettings {
    private static final String SOCK_NAME = "PermiSenseXposed";
    private static boolean loaded = false;

    public static final int UNAVAILABLE = -1;
    public static final int REVOKED = 0;
    public static final int GRANTED = 1;

    public static native void grantPermission(String packageName, String permissions);
    public static native void revokePermission(String packageName, String permissions);
    public static native int queryPermission(String packageName, char permission);
    public static native void runServer();
    public static native void killServer();
    private static native void init(String sockName);

    public static void load(String path) {
        if (loaded) return;
        System.load(path);
        init(SOCK_NAME);
        loaded = true;
    }

    public static void load() {
        if (loaded) return;
        System.loadLibrary("permisense");
        init(SOCK_NAME);
        loaded = true;
    }
}
