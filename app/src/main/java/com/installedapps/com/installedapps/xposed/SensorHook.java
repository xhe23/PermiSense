package com.installedapps.com.installedapps.xposed;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.installedapps.com.installedapps.model.PermisensePermissions;

import java.util.ArrayList;
import java.util.HashMap;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class SensorHook {
    private String packageName;
    private ClassLoader classLoader;
    private static final long CHECK_INTERVAL = 200; // ms
    private int lastCheckRes = XposedPermissionSettings.REVOKED;
    private long lastCheckTime = System.currentTimeMillis() - CHECK_INTERVAL;
    public SensorHook(XC_LoadPackage.LoadPackageParam lpparam) {
        packageName = lpparam.packageName;
        classLoader = lpparam.classLoader;
    }
    public void hookSensors() {
        XposedBridge.hookAllMethods(XposedHelpers.findClass("android.hardware.SensorManager", classLoader), "registerListener", registerHook);
        XposedBridge.hookAllMethods(XposedHelpers.findClass("android.hardware.SensorManager", classLoader), "unregisterListener", unregisterHook);
    }

    XC_MethodHook registerHook = new XC_MethodHook() {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            if (param.args[0] instanceof SensorEventListener) {
                SensorEventListener originalListener = (SensorEventListener) param.args[0];
                SensorManager sm = (SensorManager) param.thisObject;
                synchronized (wrappers) {
                    SensorListenerWrapper wrapper = getWrapper(originalListener, sm);
                    wrapper.rc++;
                    param.args[0] = wrapper;
                }
            }
        }
    };
    XC_MethodHook unregisterHook = new XC_MethodHook() {
        SensorListenerWrapper wrapper = null;
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            if (param.args[0] instanceof SensorEventListener && !(param.args[0] instanceof SensorListenerWrapper)) {
                SensorEventListener originalListener = (SensorEventListener) param.args[0];
                wrapper = getWrapper(originalListener, null);
                if (wrapper != null) param.args[0] = wrapper;
            }
        }
        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            if (wrapper != null) {
                synchronized (wrappers) {
                    wrapper.rc--;
                    if (wrapper.rc <= 0 || param.args.length == 1) {
                        wrappers.remove(wrapper);
                    }
                }
            }
        }
    };

    final HashMap<SensorEventListener, SensorListenerWrapper> wrappers = new HashMap<>();
    SensorListenerWrapper getWrapper(SensorEventListener originalListener, SensorManager sm) {
        synchronized (wrappers) {
            SensorListenerWrapper wrapper = wrappers.get(originalListener);
            if (wrapper == null) {
                wrapper = new SensorListenerWrapper(originalListener);
                wrappers.put(originalListener, wrapper);
            }
            if (wrapper != null && sm != null) wrapper.sms.add(sm);
            return wrapper;
        }
    }

    class SensorListenerWrapper implements SensorEventListener {
        public int rc = 0;
        private SensorEventListener listener;
        public ArrayList<SensorManager> sms = new ArrayList<>();
        public SensorListenerWrapper(SensorEventListener listener) {
            this.listener = listener;
        }
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            long now = System.currentTimeMillis();
            if (now - lastCheckTime > CHECK_INTERVAL) {
                int permRes = XposedPermissionSettings.queryPermission(packageName, PermisensePermissions.SENSOR);
                if (permRes != XposedPermissionSettings.UNAVAILABLE) {
                    lastCheckRes = permRes;
                    lastCheckTime = now;
                }
            }
            if (lastCheckRes == XposedPermissionSettings.GRANTED) {
                try {
                    listener.onSensorChanged(sensorEvent);
                } catch (NullPointerException e) {
                    for (SensorManager sm: sms) sm.unregisterListener(this);
                    synchronized (wrappers) {
                        wrappers.remove(this);
                    }
                }
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
            try {
                listener.onAccuracyChanged(sensor, i);
            } catch (NullPointerException e) {
                for (SensorManager sm: sms) sm.unregisterListener(this);
                synchronized (wrappers) {
                    wrappers.remove(this);
                }
            }
        }
    }
}
