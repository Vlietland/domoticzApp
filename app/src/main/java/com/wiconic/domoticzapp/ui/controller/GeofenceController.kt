package com.wiconic.domoticzapp.ui.controller

import android.content.Context
import android.util.Log
import com.wiconic.domoticzapp.model.GeofenceModel
import com.wiconic.domoticzapp.settings.AppPreferenceManager
import com.wiconic.domoticzapp.ui.controller.GateController

class GeofenceController(
    private val context: Context,
    private val preferenceManager: AppPreferenceManager,
    private val gateController: GateController
) {

    private var geofenceModel: GeofenceModel? = null
    private var isMonitoring = false

    companion object {
        private const val TAG = "GeofenceController"
    }

    fun initializeGeofence() {
        val centerLat = preferenceManager.getGeofenceCenterLat()
        val centerLon = preferenceManager.getGeofenceCenterLon()
        val radius = preferenceManager.getGeofenceRadius()
        val measurementsBeforeTrigger = 3

        geofenceModel = GeofenceModel(
            geofenceCenterLat = centerLat,
            geofenceCenterLon = centerLon,
            geofenceRadius = radius,
            measurementsBeforeTrigger = measurementsBeforeTrigger,
            controller = this  // Controller reference passed for triggering
        )

        Log.i(TAG, "Geofence initialized at ($centerLat, $centerLon) with radius $radius meters")
    }

    fun startMonitoring() {
        if (isMonitoring) return

        val geofencingEnabled = preferenceManager.getGeofenceEnabled()
        if (!geofencingEnabled) {
            Log.i(TAG, "Geofencing is disabled in settings. Not starting monitoring.")
            return
        }

        isMonitoring = true
        Log.i(TAG, "Geofence monitoring started.")
    }

    fun stopMonitoring() {
        if (!isMonitoring) return

        isMonitoring = false
        Log.i(TAG, "Geofence monitoring stopped.")
    }

    fun onGeofenceTriggered() {
        Log.i(TAG, "Handling geofence trigger event in the controller.")
        gateController.openGate()
    }
}
