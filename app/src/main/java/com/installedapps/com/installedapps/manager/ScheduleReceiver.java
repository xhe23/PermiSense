package com.installedapps.com.installedapps.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScheduleReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String scenarioName = intent.getStringExtra("scenario_name");
        boolean isStart = intent.getBooleanExtra("is_start", true);
        Log.i("PermiSense", scenarioName + " is started ? " + isStart);
    }
}
