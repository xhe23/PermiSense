package com.installedapps.com.installedapps;

import com.installedapps.com.installedapps.model.PermisensePermissions;

import java.util.Collection;

public class PermissionOperator {
    private static PermissionOperator instance;

    public static PermissionOperator getInstance() {
        if (instance==null){
            instance=new PermissionOperator();
        }
        return instance;
    }

    public int apply(Collection<PermissionOperation> operations){
        int cnt=0;
        for (PermissionOperation op:operations){
            switch (op.permisensePermission){
                case PermisensePermissions.LOCATION:
                    break;
                case PermisensePermissions.SENSOR:
                    break;
                case PermisensePermissions.MICROPHONE:
                    break;
                case PermisensePermissions.CAMERA:
                    break;
                case PermisensePermissions.SMS:
                    break;
                case PermisensePermissions.PHONE:
                    break;
            }
            ++cnt;
        }
        return cnt;
    }
}
