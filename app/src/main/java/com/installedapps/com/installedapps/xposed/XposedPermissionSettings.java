package com.installedapps.com.installedapps.xposed;

public class XposedPermissionSettings {
    public static final String SOCK_NAME = "PermiSenseXposed";

    public static native void grantPermission(String packageName, String permissions);
    public static native void revokePermission(String packageName, String permissions);
    public static native int queryPermission(String packageName, char permission);
    public static native void runServer();
    public static native void killServer();
    private static native void init(String sockName);

    static {
        System.loadLibrary("permisense");
        init(SOCK_NAME);
    }
}
