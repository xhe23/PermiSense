package com.installedapps.com.installedapps;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.installedapps.com.installedapps.dao.RuleDao;
import com.installedapps.com.installedapps.model.AppGroup;
import com.installedapps.com.installedapps.model.Rule;
import com.installedapps.com.installedapps.model.Scenario;

@Database(entities = {Rule.class, AppGroup.class, Scenario.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract RuleDao ruleDao();
    private static AppDatabase instance=null;

    public static AppDatabase getInstance(Context appContext){
        if (instance==null){
            instance= Room.databaseBuilder(appContext,AppDatabase.class,"permisense").build();
        }
        return instance;
    }

    protected AppDatabase(){
    }
}
