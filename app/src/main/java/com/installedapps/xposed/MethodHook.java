package com.installedapps.xposed;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public class MethodHook extends XC_MethodHook {
    final static int RETURN = 0;
    final static int THROW = 1;

    final static int SENSOR = 0;

    private int mCategory;
    private int mResponseType;
    private Object mResult;
    private String packageName;

    public MethodHook(int category, int responseType, Object result) {
        super();
        mCategory = category;
        mResponseType = responseType;
        mResult = result;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        // todo: check if permitted
        //// or use unhook
        // if (deny) {
        XposedBridge.log(String.format("Method call blocked: %s (%s)", param.method.getName(), packageName));
        if (mResponseType == RETURN) {
            param.setResult(mResult);
        }
        // }
    }
}
