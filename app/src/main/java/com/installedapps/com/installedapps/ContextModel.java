package com.installedapps.com.installedapps;

import android.graphics.drawable.Drawable;

import java.util.Date;

public class ContextModel {
    private AppModel App;
    private GeoLocation Location;
    private Date StartTime, EndTime;

    public ContextModel(AppModel app, Date startTime, Date endTime, GeoLocation location) {
        this.App = app;
        this.Location = location;
        this.StartTime = startTime;
        this.EndTime = endTime;
    }

    public AppModel getApp() { return App; }

    public GeoLocation getLocation() { return Location; }

    public Date getStartTime() { return StartTime; }

    public Date getEndTime() { return EndTime; }
}
