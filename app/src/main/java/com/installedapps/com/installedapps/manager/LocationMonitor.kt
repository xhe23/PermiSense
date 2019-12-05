package com.installedapps.com.installedapps.manager

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.installedapps.com.installedapps.model.Scenario
import com.installedapps.com.installedapps.model.ScenarioLocationDef
import java.util.concurrent.ConcurrentHashMap

class LocationMonitor(s: Scenario) : ScenarioMonitor(s.name) {
    private var definition: ScenarioLocationDef = s.definition as ScenarioLocationDef
    init {
        Log.i("PermiSense", String.format("Create location monitor, name = %s, lat = %f, lon = %f, r = %f", name, definition.lat, definition.lon, definition.radius))
    }

    private val genfencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofences(listOf(
                    Geofence.Builder()
                            .setRequestId(name)
                            .setCircularRegion(definition.lat, definition.lon, definition.radius.toFloat())
                            .setExpirationDuration(Geofence.NEVER_EXPIRE)
                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                            .build()
            ))
            .build()

    override fun startMonitor() {
        val permissionManager = PermissionManager.getInstance()
        val geofencingClient = LocationServices.getGeofencingClient(permissionManager.context)

        val intent = Intent(permissionManager.context, LocationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(permissionManager.context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        Log.i("PermiSense", "location monitor " + name + " enabled")
        enabledMonitors[name] = this
        geofencingClient.addGeofences(genfencingRequest, pendingIntent)
                .addOnFailureListener {
                    Log.e("PermiSense", "Failed to add geofence", it)
                    if (it is ApiException && it.statusCode == GEOFENCE_NOT_AVAILABLE) {
                        Toast.makeText(permissionManager.context, "Please enable Google Location services!", Toast.LENGTH_SHORT)
                    }
                    enabledMonitors.remove(name)
                }
    }

    override fun stopMonitor() {
        val permissionManager = PermissionManager.getInstance()
        val geofencingClient = LocationServices.getGeofencingClient(permissionManager.context)
        enabledMonitors.remove(name)
        geofencingClient.removeGeofences(listOf(name))
                .addOnFailureListener {
                    Log.e("PermiSense", "Failed to remove geofence", it)
                }
    }

    private fun handleEvent(transition: Int) {
        val permissionManager = PermissionManager.getInstance()
        if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            activated = true
            Log.i("PermiSense", "enter gerofence " + name)
        } else if (transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            activated = false
            Log.i("PermiSense", "leave gerofence " + name)
        }
        permissionManager.onMonitorStateChanged(this)
    }

    companion object {
        private val enabledMonitors = ConcurrentHashMap<String, LocationMonitor>()
        fun onEvent(name: String, transition: Int) {
            val monitor = enabledMonitors[name]
            if (monitor == null) {
                Log.e("PermiSense", "geofence broadcast unknown fense: " + name + " (removed)")
                val permissionManager = PermissionManager.getInstance()
                val geofencingClient = LocationServices.getGeofencingClient(permissionManager.context)
                geofencingClient.removeGeofences(listOf(name))
                return
            }
            if (transition == Geofence.GEOFENCE_TRANSITION_ENTER || transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                monitor.handleEvent(transition)
            } else {
                Log.e("PermiSense", "geofence broadcast wrong transition: " + transition)
            }
        }
    }
}