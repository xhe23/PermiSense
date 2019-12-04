package com.installedapps.com.installedapps.manager;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.installedapps.com.installedapps.model.Scenario;

import java.util.ArrayList;
import java.util.List;

public class PermissionManager {
    private static PermissionManager instance = null;

    public static PermissionManager getInstance() {
        if (instance == null) {
            synchronized (PermissionManager.class) {
                if (instance == null) {
                    instance = new PermissionManager();
                }
            }
        }
        return instance;
    }

    private PermissionManager() {}

    Context context = null;
    public Context getContext() { return context; }
    public void setContext(Context context) { this.context = context; }

    public void onMonitorStateChanged(ScenarioMonitor monitor) {
        // todo
        String msg = "Monitor " + monitor.getName() + " changed";
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    private ArrayList<ScenarioMonitor> monitors = new ArrayList<>();

    public void onLoadScenarios(List<Scenario> scenarios) {
        // todo: check which monitors need to be updated
        for (ScenarioMonitor m: monitors) {
            m.stopMonitor();
        }
        monitors.clear();
        for (Scenario s: scenarios) {
            ScenarioMonitor m = ScenarioMonitor.create(s);
            monitors.add(m);
            m.startMonitor();
        }
    }
}
