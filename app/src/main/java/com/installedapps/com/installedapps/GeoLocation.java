package com.installedapps.com.installedapps;

public class GeoLocation {
    private Double Latitude;
    private Double Longitude;

    public GeoLocation(double latitude, double longitude) {
        this.Latitude = latitude;
        this.Longitude = longitude;
    }

    public double getLat() {
        return Latitude;
    }

    public double getLong() {
        return Longitude;
    }
}
