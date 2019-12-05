package com.installedapps.com.installedapps;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

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

    private Process su=null;
    private DataOutputStream os=null;

    public int open(){
        try {
            su=Runtime.getRuntime().exec("su");
            os=new DataOutputStream(su.getOutputStream());
        }catch (IOException e){
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    public int close(){
        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(su.getInputStream()));
        String s = null;
        try {
            os.flush();
            su.destroy();
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }
            os=null;
            su=null;
        }catch (IOException e){
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    public int addGrant(String packageName, String manifestPermission){
        if (os==null) return -1;
        try {
            os.writeBytes(String.format("pm grant %s %s",packageName,manifestPermission));
        }catch (IOException e){
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    public int addRevoke(String packageName, String manifestPermission){
        if (os==null) return -1;
        try {
            os.writeBytes(String.format("pm revoke %s %s",packageName,manifestPermission));
        }catch (IOException e){
            e.printStackTrace();
            return -1;
        }
        return 0;
    }
}
