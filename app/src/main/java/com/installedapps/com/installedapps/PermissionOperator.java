package com.installedapps.com.installedapps;

import android.Manifest;

import com.installedapps.com.installedapps.model.PermisensePermissions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PermissionOperator {
    private static PermissionOperator instance;
    public static PermissionOperator getInstance() {
        if (instance==null){
            instance=new PermissionOperator();
        }
        return instance;
    }

    public static void setOriginalPermissions(Map<String, Set<String>> originalPermissions) {
        instance.originalPermissions = originalPermissions;
    }

    private Map<String, Set<String>> originalPermissions;

    PermissionOperator() {
        //TODO initialize originalPermissions properly from external?
        originalPermissions=new HashMap<>();
    }

    public int apply(Collection<PermissionOperation> operations){
        int cnt=0;
        PermissionManagerSu managerSu=new PermissionManagerSu();
        managerSu.open();
        for (PermissionOperation op:operations){
            Set<String> userPerms=originalPermissions.get(op.packageName);
            if (userPerms==null){
                //TODO initialize it properly
                //Newly installed app? newly granted permission?
                return -1;
            }
            if (op.op==PermissionOperation.REVOKE){
                switch (op.permisensePermission){
                    case PermisensePermissions.LOCATION:
                        if (userPerms.contains(Manifest.permission.ACCESS_COARSE_LOCATION))
                            managerSu.addRevoke(op.packageName, Manifest.permission.ACCESS_COARSE_LOCATION);
                        if (userPerms.contains(Manifest.permission.ACCESS_FINE_LOCATION))
                            managerSu.addRevoke(op.packageName, Manifest.permission.ACCESS_FINE_LOCATION);
                        break;
                    case PermisensePermissions.SENSOR:
                        break;
                    case PermisensePermissions.MICROPHONE:
                        if (userPerms.contains(Manifest.permission.RECORD_AUDIO))
                            managerSu.addRevoke(op.packageName, Manifest.permission.RECORD_AUDIO);
                        break;
                    case PermisensePermissions.CAMERA:
                        if (userPerms.contains(Manifest.permission.CAMERA))
                            managerSu.addRevoke(op.packageName, Manifest.permission.CAMERA);
                        break;
                    case PermisensePermissions.SMS:
                        if (userPerms.contains(Manifest.permission.SEND_SMS))
                            managerSu.addRevoke(op.packageName, Manifest.permission.SEND_SMS);
                        if (userPerms.contains(Manifest.permission.BROADCAST_SMS))
                            managerSu.addRevoke(op.packageName, Manifest.permission.BROADCAST_SMS);
                        if (userPerms.contains(Manifest.permission.READ_SMS))
                            managerSu.addRevoke(op.packageName, Manifest.permission.READ_SMS);
                        if (userPerms.contains(Manifest.permission.RECEIVE_SMS))
                            managerSu.addRevoke(op.packageName, Manifest.permission.RECEIVE_SMS);
                        break;
                    case PermisensePermissions.PHONE:
                        if (userPerms.contains(Manifest.permission.ANSWER_PHONE_CALLS))
                            managerSu.addRevoke(op.packageName, Manifest.permission.ANSWER_PHONE_CALLS);
                        if (userPerms.contains(Manifest.permission.CALL_PHONE))
                            managerSu.addRevoke(op.packageName, Manifest.permission.CALL_PHONE);
                        break;
                }
            }else{
                switch (op.permisensePermission){
                    case PermisensePermissions.LOCATION:
                        if (userPerms.contains(Manifest.permission.ACCESS_COARSE_LOCATION))
                            managerSu.addGrant(op.packageName, Manifest.permission.ACCESS_COARSE_LOCATION);
                        if (userPerms.contains(Manifest.permission.ACCESS_FINE_LOCATION))
                            managerSu.addGrant(op.packageName, Manifest.permission.ACCESS_FINE_LOCATION);
                        break;
                    case PermisensePermissions.SENSOR:
                        break;
                    case PermisensePermissions.MICROPHONE:
                        if (userPerms.contains(Manifest.permission.RECORD_AUDIO))
                            managerSu.addGrant(op.packageName, Manifest.permission.RECORD_AUDIO);
                        break;
                    case PermisensePermissions.CAMERA:
                        if (userPerms.contains(Manifest.permission.CAMERA))
                            managerSu.addGrant(op.packageName, Manifest.permission.CAMERA);
                        break;
                    case PermisensePermissions.SMS:
                        if (userPerms.contains(Manifest.permission.SEND_SMS))
                            managerSu.addGrant(op.packageName, Manifest.permission.SEND_SMS);
                        if (userPerms.contains(Manifest.permission.BROADCAST_SMS))
                            managerSu.addGrant(op.packageName, Manifest.permission.BROADCAST_SMS);
                        if (userPerms.contains(Manifest.permission.READ_SMS))
                            managerSu.addGrant(op.packageName, Manifest.permission.READ_SMS);
                        if (userPerms.contains(Manifest.permission.RECEIVE_SMS))
                            managerSu.addGrant(op.packageName, Manifest.permission.RECEIVE_SMS);
                        break;
                    case PermisensePermissions.PHONE:
                        if (userPerms.contains(Manifest.permission.ANSWER_PHONE_CALLS))
                            managerSu.addGrant(op.packageName, Manifest.permission.ANSWER_PHONE_CALLS);
                        if (userPerms.contains(Manifest.permission.CALL_PHONE))
                            managerSu.addGrant(op.packageName, Manifest.permission.CALL_PHONE);
                        break;
                }
            }
            ++cnt;
        }
        managerSu.close();
        return cnt;
    }
}
