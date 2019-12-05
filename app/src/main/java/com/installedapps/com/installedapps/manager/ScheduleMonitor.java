package com.installedapps.com.installedapps.manager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.installedapps.com.installedapps.model.Scenario;
import com.installedapps.com.installedapps.model.ScenarioDef;
import com.installedapps.com.installedapps.model.ScenarioTimeDef;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ScheduleMonitor extends ScenarioMonitor {
    private static final long MILLIS_IN_DAY = 86400000L;
    private static final long YEAR_2100 = 4102473600000L;
    private static final long ONE_HUNDRED_YEARS = 3153600000000L;
    private ScenarioTimeDef definition;

    private static Map<String, ScheduleMonitor> enabledMonitors = new ConcurrentHashMap<String, ScheduleMonitor>();

    public ScheduleMonitor(Scenario s) {
        super(s.name);
        definition = (ScenarioTimeDef)s.definition;
        activated = false;
    }

    @Override
    public void startMonitor() {
        if(definition.startTime < System.currentTimeMillis()) {
            return;
        }
        PermissionManager permissionManager = PermissionManager.getInstance();
        AlarmManager m1 = (AlarmManager)permissionManager.context.getSystemService(Context.ALARM_SERVICE);
        AlarmManager m2 = (AlarmManager)permissionManager.context.getSystemService(Context.ALARM_SERVICE);

        Intent startIntent = new Intent(permissionManager.context, ScheduleReceiver.class);
        startIntent.putExtra("is_start", true);
        startIntent.putExtra("scenario_name", this.getName());
        PendingIntent p1 = PendingIntent.getBroadcast(permissionManager.context, (int)System.currentTimeMillis(), startIntent, PendingIntent.FLAG_ONE_SHOT);
        if(definition.startTime > YEAR_2100) {
            m1.setRepeating(AlarmManager.RTC_WAKEUP, definition.startTime - ONE_HUNDRED_YEARS, MILLIS_IN_DAY, p1);
            Log.i("PermiSense", "added start time " + (definition.startTime - ONE_HUNDRED_YEARS));
        } else {
            m1.setExact(AlarmManager.RTC_WAKEUP, definition.startTime, p1);
            Log.i("PermiSense", "added start time " + definition.startTime);
        }

        Intent endIntent = new Intent(permissionManager.context, ScheduleReceiver.class);
        endIntent.putExtra("is_start", false);
        endIntent.putExtra("scenario_name", this.getName());
        PendingIntent p2 = PendingIntent.getBroadcast(permissionManager.context, (int)System.currentTimeMillis(), endIntent, PendingIntent.FLAG_ONE_SHOT);
        if(definition.startTime > YEAR_2100) {
            m2.setRepeating(AlarmManager.RTC_WAKEUP, definition.endTime, MILLIS_IN_DAY, p2);
        } else {
            m2.setExact(AlarmManager.RTC_WAKEUP, definition.endTime, p2);
        }
        Log.i("PermiSense", "added end time " + definition.endTime);


        Log.i("PermiSense", "schedule monitor " + this.getName() + " enabled");
        ScheduleMonitor.enabledMonitors.put(this.getName(), this);
    }

    @Override
    public void stopMonitor() {

    }

    public static void onEvent(String name, boolean isStart) {
        ScheduleMonitor monitor = enabledMonitors.get(name);
        if (monitor == null) {
            Log.e("PermiSense", "unknown schedule: " + name + " (removed)");
            PermissionManager permissionManager = PermissionManager.getInstance();
            return;
        }
        monitor.activated = isStart;
        PermissionManager permissionManager = PermissionManager.getInstance();
        permissionManager.onMonitorStateChanged(monitor);
        Log.i("PermiSense", "Scenario: " + name + " is set to " + isStart);
    }
}
