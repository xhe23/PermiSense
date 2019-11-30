package com.installedapps.com.installedapps;

import java.io.IOException;

public class PermissionManagerSu {
    public static int grantPermission(String packageName, String manifestPermission){
        try {
            Process pm= Runtime.getRuntime().exec(new String[]{"su","-c", String.format("pm grant %s %s",packageName,manifestPermission)});
        }catch (IOException e){
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    public static int revokePermission(String packageName, String manifestPermission){
        try {
            Process pm= Runtime.getRuntime().exec(new String[]{"su","-c", String.format("pm revoke %s %s",packageName,manifestPermission)});
        }catch (IOException e){
            e.printStackTrace();
            return -1;
        }
        return 0;
    }
}
