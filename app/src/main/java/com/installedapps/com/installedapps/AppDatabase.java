package com.installedapps.com.installedapps;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.installedapps.com.installedapps.dao.AppgroupDao;
import com.installedapps.com.installedapps.dao.RuleDao;
import com.installedapps.com.installedapps.dao.ScenarioDao;
import com.installedapps.com.installedapps.model.AppGroup;
import com.installedapps.com.installedapps.model.Rule;
import com.installedapps.com.installedapps.model.Scenario;
import com.installedapps.com.installedapps.model.ScenarioDef;

@Database(entities = {Rule.class, AppGroup.class, Scenario.class}, version = 1)
@TypeConverters({ScenarioDef.Converter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract RuleDao ruleDao();
    public abstract ScenarioDao scenarioDao();
    public abstract AppgroupDao appgroupDao();
    private static AppDatabase instance = null;

    public static AppDatabase getInstance(Context appContext){
        if (instance == null){
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(appContext,AppDatabase.class,"permisense").build();
                }
            }
        }
        return instance;
    }

    protected AppDatabase(){
    }
}
