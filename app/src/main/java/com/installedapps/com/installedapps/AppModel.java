package com.installedapps.com.installedapps;

/*
 *  ****************************************************************************
 *  * Created by : Md. Moniruzzaman Monir on 11/16/2018 at 6:57 PM.
 *  * Email : moniruzzaman@w3engineers.com
 *  *
 *  * Purpose:
 *  *
 *  * Last edited by : Md. Moniruzzaman Monir on 11/16/2018.
 *  *
 *  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>
 *  ****************************************************************************
 */

import android.graphics.drawable.Drawable;

public class AppModel {
    private String name;
    Drawable icon;
    private String packageName;

    public AppModel(String name, Drawable icon, String packageName) {
        this.name = name;
        this.icon = icon;
        this.packageName = packageName;
    }

    public String getName() {
        return name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public String getPackageName() { return packageName; }
}
