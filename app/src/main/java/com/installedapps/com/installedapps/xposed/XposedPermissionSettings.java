package com.installedapps.com.installedapps.xposed;

public class XposedPermissionSettings {
    private static final String SOCK_NAME = "PermiSenseXposed";

    public static final int UNAVAILABLE = -1;
    public static final int REVOKED = 0;
    public static final int GRANTED = 1;

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
