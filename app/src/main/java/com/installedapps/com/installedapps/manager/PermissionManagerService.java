package com.installedapps.com.installedapps.manager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.installedapps.com.installedapps.AppDatabase;
import com.installedapps.com.installedapps.model.Scenario;
import com.installedapps.com.installedapps.xposed.XposedPermissionSettings;

import java.util.List;


public class PermissionManagerService extends Service {

    public static final int NOTIF_ID = 114514;

    private final IPermissionManagerService.Stub binder = new IPermissionManagerService.Stub() {
        @Override
        public void reloadScenarios() throws RemoteException {
            PermissionManagerService.this.reloadScenarios();
        }
    };

    @Override
    public void onCreate() {
        PermissionManager.getInstance().setContext(this);
        XposedPermissionSettings.runServer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("manager", "Permission Manager", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Permission Manager Service");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            startForeground(NOTIF_ID, new Notification.Builder(this, "manager").build());
        } else {
            startForeground(NOTIF_ID, new Notification.Builder(this).build());
        }

        reloadScenarios();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        PermissionManager.getInstance().setContext(null);
        XposedPermissionSettings.killServer();
    }

    private void reloadScenarios() {
        new ReloadScenarioTask().execute(this);
    }

    private static class ReloadScenarioTask extends AsyncTask<PermissionManagerService, Void, List<Scenario>> {
        @Override
        protected List<Scenario> doInBackground(PermissionManagerService... services) {
            return AppDatabase.getInstance(services[0]).scenarioDao().getAll();
        }
        @Override
        protected void onPostExecute(List<Scenario> scenarios) {
            PermissionManager.getInstance().onLoadScenarios(scenarios);
        }
    }
}
