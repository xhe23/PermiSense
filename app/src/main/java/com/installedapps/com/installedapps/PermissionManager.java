package com.installedapps.com.installedapps;

public class PermissionManager {
    public static boolean checkPermission(String packageName, int type) {
        return System.currentTimeMillis() / 10000 % 2 == 1;
    }
}
