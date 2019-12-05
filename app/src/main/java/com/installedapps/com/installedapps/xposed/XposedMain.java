package com.installedapps.com.installedapps.xposed;

import android.content.pm.ApplicationInfo;
import android.util.Log;

import com.installedapps.com.installedapps.BuildConfig;

import java.io.File;
import java.util.HashMap;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XposedMain implements IXposedHookLoadPackage, IXposedHookZygoteInit {
    private HashMap<String, SensorHook> sensorHooks = new HashMap<>();
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName == null || lpparam.packageName.equals("android") ||
                lpparam.appInfo == null || (lpparam.appInfo.flags & (ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) != 0 ||
                lpparam.packageName.startsWith(BuildConfig.APPLICATION_ID))
            return;
        hookApplication(lpparam);
    }

    private static String modulePath = null;
    private static String nativeLibPath = null;

    private void hookApplication(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        nativeLibPath = mixLibPath(modulePath, lpparam.appInfo.nativeLibraryDir);
        Log.i("PermiSenseHook", "native lib: " + nativeLibPath);
        XposedPermissionSettings.load(nativeLibPath);
        SensorHook sensorHook = new SensorHook(lpparam);
        sensorHook.hookSensors();
        sensorHooks.put(lpparam.packageName, sensorHook);
    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        modulePath = startupParam.modulePath;
    }

    private static String mixLibPath(String modulePath, String nativeLibDir) {
        if (modulePath == null || nativeLibDir == null) return null;
        File lp = new File(nativeLibDir);
        File lpp = lp.getParentFile();
        if (lpp == null || !lpp.getName().equals("lib")) return null;
        String arch = lp.getName();
        File mp = new File(modulePath);
        File mpp = mp.getParentFile();
        if (mpp == null) return null;
        return mpp.getAbsolutePath() + "/lib/" + arch + "/libpermisense.so";
    }

    public static String getNativeLibPath() {
        return nativeLibPath;
    }
}
