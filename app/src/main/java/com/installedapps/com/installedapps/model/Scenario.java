package com.installedapps.com.installedapps.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Scenario {
    @PrimaryKey
    @NonNull
    public String name;

    public ScenarioDef definition;
}
