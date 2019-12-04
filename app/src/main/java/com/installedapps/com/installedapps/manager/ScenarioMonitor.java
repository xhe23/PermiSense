package com.installedapps.com.installedapps.manager;

import com.installedapps.com.installedapps.model.Scenario;
import com.installedapps.com.installedapps.model.ScenarioLocationDef;

public abstract class ScenarioMonitor {
    private String name;
    protected String getName() {
        return name;
    }
    public ScenarioMonitor(String name) {
        this.name = name;
    }
    public abstract void startMonitor();
    public abstract void stopMonitor();

    public static ScenarioMonitor create(Scenario s) {
        if (s.definition instanceof ScenarioLocationDef) {
            return new LocationMonitor(s);
        }
        throw new UnsupportedOperationException("Undefined scenario type: " + s.definition.getClass().getName());
    }
}
