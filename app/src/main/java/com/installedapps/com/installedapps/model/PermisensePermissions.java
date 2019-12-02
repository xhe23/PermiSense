package com.installedapps.com.installedapps.model;

public class PermisensePermissions {
    public static final char LOCATION='A';
    public static final char SENSOR='B';
    public static final char MICROPHONE='C';
    public static final char CAMERA='D';
    public static final char SMS='E';
    public static final char PHONE='F';

    public static final String[] names={"Location","Sensor","Microphone","Camera","SMS","Phone"};

    static public char index2perm(int index){
        if (index>=names.length)
            throw new RuntimeException("Permission index out of range!");
        return (char)('A'+index);
    }
}
