package com.wiconic.domoticzapp.ui.observer

import android.content.Context
import android.content.SharedPreferences
import com.wiconic.domoticzapp.settings.DomoticzPreferenceManager
import com.wiconic.domoticzapp.settings.GeofencePreferenceManager
import com.wiconic.domoticzapp.ui.controller.GeofenceController
import com.wiconic.domoticzapp.api.AlarmPushHandler

class PreferenceObserver(
    private val context: Context,
    private val preferenceManager: DomoticzPreferenceManager,
    private val geofencePreferenceManager: GeofencePreferenceManager,
    private val geofenceController: GeofenceController,
    private val alarmPushHandler: AlarmPushHandler
) : SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            "server_ip", "server_port" -> {
                alarmPushHandler.apply {
                    cleanup()
                    initialize(context, preferenceManager.getWebSocketUrl())
                }
            }
            "geofence_enabled" -> {
                if (geofencePreferenceManager.isGeofenceEnabled()) {
                    geofenceController.initializeGeofence()
                } else {
                    geofenceController.removeGeofence()
                }
            }
            "geofence_lat",
            "geofence_lon",
            "geofence_radius",
            "polling_frequency",
            "measurements_before_trigger" -> {
                if (geofencePreferenceManager.isGeofenceEnabled()) {
                    geofenceController.initializeGeofence()
                }
            }
        }
    }
}
