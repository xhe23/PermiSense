package com.installedapps.com.installedapps;

public class PermissionOperation {
    public static final int REVOKE=0;
    public static final int GRANT=1;

    public String packageName;
    public int op;
    public char permisensePermission;

    public PermissionOperation(String packageName, int op, char permisensePermission) {
        this.packageName = packageName;
        this.op = op;
        this.permisensePermission = permisensePermission;
    }
}
