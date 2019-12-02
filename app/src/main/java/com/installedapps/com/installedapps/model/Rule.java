package com.installedapps.com.installedapps.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Rule {
    @PrimaryKey(autoGenerate = true)
    public long ruleId;

    @ColumnInfo(name="group_name")
    public String groupName;

    @ColumnInfo(name="scenario_name")
    public String scenarioName;

    @ColumnInfo(name="permissions")
    public String permissions;
}
