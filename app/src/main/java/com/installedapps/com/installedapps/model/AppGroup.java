package com.installedapps.com.installedapps.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class AppGroup {
    @PrimaryKey
    @NonNull
    public String groupName;
}
