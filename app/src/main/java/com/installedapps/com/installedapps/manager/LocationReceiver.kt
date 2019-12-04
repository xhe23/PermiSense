package com.installedapps.com.installedapps.manager

import android.content.*
import android.util.Log
import com.google.android.gms.location.GeofencingEvent

class LocationReceiver: BroadcastReceiver() {
    lateinit var conn: ServiceConnection
    override fun onReceive(context: Context, intent: Intent?) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            Log.e("PermiSense", "geofence broadcast error: " + geofencingEvent.errorCode)
            return
        }

        val geofenceTransition = geofencingEvent.geofenceTransition
        val triggeringGeofences = geofencingEvent.triggeringGeofences
        for (geofence in triggeringGeofences) {
            LocationMonitor.onEvent(geofence.requestId, geofenceTransition)
        }
    }
}