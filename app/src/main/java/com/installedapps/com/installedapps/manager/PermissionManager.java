package com.installedapps.com.installedapps.manager;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.installedapps.com.installedapps.AppDatabase;
import com.installedapps.com.installedapps.PermissionOperation;
import com.installedapps.com.installedapps.PermissionOperator;
import com.installedapps.com.installedapps.model.AppGroup;
import com.installedapps.com.installedapps.model.Rule;
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

    private class ApplyPermissionTask extends AsyncTask<Object,Void,Void>{

        private String appGroupName;
        private String permissions;
        private int operation;

        public ApplyPermissionTask(String appGroupName, String permissions, int operation) {
            this.appGroupName = appGroupName;
            this.permissions = permissions;
            this.operation = operation;
        }

        @Override
        protected Void doInBackground(Object... objects) {
            AppDatabase db=AppDatabase.getInstance(context);
            List<AppGroup> groups=db.appgroupDao().getAll();
            for (AppGroup g:groups){
                if (g.getGroupName().equals(appGroupName)){
                    String[] targets=g.apps.split("#");
                    for (String target:targets){
                        if (target==null || target.length()==0) continue;
                        List<PermissionOperation> operations=new ArrayList<>();
                        for (int i=0;i<permissions.length();++i)
                            operations.add(new PermissionOperation(target,operation,permissions.charAt(i)));
                        PermissionOperator.getInstance().apply(operations);
                    }
                }
            }
            return null;
        }
    }

    public void onMonitorStateChanged(ScenarioMonitor monitor) {
        // TODO: calculate if granting is a good choice by refcnt, etc.
        if (monitor.isActivated()){
            for (Rule r:rules){
                if (r.scenarioName.equals(monitor.getName())){
                    new ApplyPermissionTask(r.groupName,r.permissions, PermissionOperation.REVOKE).execute();
                }
            }
        }else{
            for (Rule r:rules){
                if (r.scenarioName.equals(monitor.getName())){
                    new ApplyPermissionTask(r.groupName,r.permissions, PermissionOperation.GRANT).execute();
                }
            }
        }
        String msg = "Monitor " + monitor.getName() + " changed";
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    private ArrayList<ScenarioMonitor> monitors = new ArrayList<>();
    private List<Rule> rules = new ArrayList<>();

    public void addRule(Rule rule){
        for (ScenarioMonitor sm:monitors){
            if (sm.getName().equals(rule.scenarioName)){
                if (sm.isActivated())
                    new ApplyPermissionTask(rule.groupName, rule.permissions, PermissionOperation.REVOKE).execute();
                rules.add(rule);
                return;
            }
        }
        throw new RuntimeException("Scenario not monitored");
    }

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
