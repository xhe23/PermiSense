package com.installedapps.com.installedapps.xposed;

import android.content.pm.ApplicationInfo;
import android.os.Build;

import com.installedapps.com.installedapps.BuildConfig;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class XposedMain implements IXposedHookLoadPackage {
    private SensorHook sensorHook = null;
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName == null || lpparam.packageName.equals("android") ||
                lpparam.appInfo == null || (lpparam.appInfo.flags & (ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) != 0 ||
                lpparam.packageName.startsWith(BuildConfig.APPLICATION_ID)) return;
        hookApplication(lpparam);
    }

    private void hookApplication(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        sensorHook = new SensorHook(lpparam);
        sensorHook.hookSensors();
    }
}
