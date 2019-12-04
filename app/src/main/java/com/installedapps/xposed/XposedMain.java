package com.installedapps.xposed;

import android.os.Build;

import com.installedapps.com.installedapps.BuildConfig;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class XposedMain implements IXposedHookLoadPackage {
    private SensorHook sensorHook = null;
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!"android".equals(lpparam.packageName) &&
                !lpparam.packageName.startsWith(BuildConfig.APPLICATION_ID))
            hookApplication(lpparam);
    }

    private void hookApplication(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        sensorHook = new SensorHook(lpparam);
        sensorHook.hookSensors();
    }

    public static void hookMethod(String className, String methodName, Object[] paramTypes, XC_LoadPackage.LoadPackageParam lpparam, int minSdk, int maxSdk, MethodHook hook) {
        if (Build.VERSION.SDK_INT < minSdk || Build.VERSION.SDK_INT > maxSdk) {
            return;
        }
        hook.setPackageName(lpparam.packageName);
        Object[] paramAndHook = new Object[paramTypes.length + 1];
        System.arraycopy(paramTypes, 0, paramAndHook, 0, paramTypes.length);
        paramAndHook[paramAndHook.length - 1] = hook;
        findAndHookMethod(className, lpparam.classLoader, methodName, paramAndHook);
    }
}
