package com.installedapps.com.installedapps.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.*;

@Entity
public class AppGroup {
    @PrimaryKey
    @NonNull
    public String groupName;

    public String apps;

    public AppGroup(String groupName, String apps) {
        this.groupName = groupName;
        this.apps = apps;
    }

    public String getGroupName() {
        return groupName;
    }
}
