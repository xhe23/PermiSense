package com.installedapps.com.installedapps;

import android.Manifest;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.installedapps.com.installedapps.model.PermisensePermissions;
import com.installedapps.com.installedapps.xposed.XposedPermissionSettings;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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

    private Map<String, Set<String>> originalPermissions;

    PermissionOperator() {
    }

    static void updateOriginalPermissions(PackageManager packageManager){
        List<PackageInfo> packages=packageManager.getInstalledPackages(0);
        Map<String, Set<String>> p=new HashMap<>();

        for (PackageInfo pkg:packages){
            if ((pkg.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0)
                continue;
            try {
                PackageInfo info = packageManager.getPackageInfo(pkg.packageName, PackageManager.GET_PERMISSIONS);
                Set<String> perms=new HashSet<>();
                if (info.requestedPermissionsFlags==null) continue;
                for (int i=0;i<info.requestedPermissionsFlags.length;++i){
                    if ((info.requestedPermissionsFlags[i] & PackageInfo.REQUESTED_PERMISSION_GRANTED)!=0){
                        perms.add(info.requestedPermissions[i]);
                    }
                }
                p.put(info.packageName,perms);
            }catch (PackageManager.NameNotFoundException ignored){}
        }
        getInstance().originalPermissions=p;
    }

    public synchronized int apply(Collection<PermissionOperation> operations){
        int cnt=0;
        PermissionManagerSu managerSu=new PermissionManagerSu();
        managerSu.open();
        for (PermissionOperation op:operations){
            Set<String> userPerms=originalPermissions.get(op.packageName);
            if (userPerms==null)
                throw new RuntimeException(String.format("Original permissions N/A for %s",op.packageName));
            if (op.op==PermissionOperation.GRANT){
                switch (op.permisensePermission){
                    case PermisensePermissions.LOCATION:
                        if (userPerms.contains(Manifest.permission.ACCESS_COARSE_LOCATION))
                            managerSu.addGrant(op.packageName, Manifest.permission.ACCESS_COARSE_LOCATION);
                        if (userPerms.contains(Manifest.permission.ACCESS_FINE_LOCATION))
                            managerSu.addGrant(op.packageName, Manifest.permission.ACCESS_FINE_LOCATION);
                        for (String permission:userPerms){
                            managerSu.addGrant(op.packageName,permission);
                        }
                        break;
                    case PermisensePermissions.SENSOR:
                        XposedPermissionSettings.grantPermission(op.packageName, String.valueOf(PermisensePermissions.SENSOR));
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
            }else{
                switch (op.permisensePermission){
                    case PermisensePermissions.LOCATION:
                        if (userPerms.contains(Manifest.permission.ACCESS_COARSE_LOCATION))
                            managerSu.addRevoke(op.packageName, Manifest.permission.ACCESS_COARSE_LOCATION);
                        if (userPerms.contains(Manifest.permission.ACCESS_FINE_LOCATION))
                            managerSu.addRevoke(op.packageName, Manifest.permission.ACCESS_FINE_LOCATION);
                        for (String permission:userPerms){
                            managerSu.addRevoke(op.packageName,permission);
                        }
                        break;
                    case PermisensePermissions.SENSOR:
                        XposedPermissionSettings.revokePermission(op.packageName, String.valueOf(PermisensePermissions.SENSOR));
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
            }
            ++cnt;
        }
        managerSu.close();
        return cnt;
    }
}
