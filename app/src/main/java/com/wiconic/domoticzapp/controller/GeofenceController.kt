package com.wiconic.domoticzapp.controller

import android.content.Context
import android.util.Log
import com.wiconic.domoticzapp.model.Geofence
import com.wiconic.domoticzapp.model.AppPreferences

class GeofenceController(
    private val context: Context,
    private val openGate: () -> Unit,
    private val appPreferences: AppPreferences) {

    private var isMonitoring = false
    private val TAG = "GeofenceController"

    fun initializeGeofence() {
        isMonitoring = true
        Log.i(TAG, "Geofence started.")
    }

    fun stopGeofence() {
        isMonitoring = false
        Log.i(TAG, "Geofence stopped.")
    }

    fun onGeofenceCallback(withinGeofence: Boolean) {
        Log.i(TAG, "Handling geofence trigger event in the controller.")
        if (withinGeofence) openGate()
    }
}
