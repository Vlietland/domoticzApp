package com.wiconic.domoticzapp.ui.observer

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.wiconic.domoticzapp.settings.AppPreferenceManager
import com.wiconic.domoticzapp.ui.controller.GeofenceController
import com.wiconic.domoticzapp.api.DomoticzAppServerConnection

class PreferenceObserver(
    private val context: Context,
    private val appPreferenceManager: AppPreferenceManager,
    private val geofenceController: GeofenceController,
    private val serverConnection: DomoticzAppServerConnection
) : SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            AppPreferenceManager.KEY_SERVER_IP_ADDRESS,
            AppPreferenceManager.KEY_SERVER_PORT -> {
                Log.i("PreferenceObserver", "Server connection settings changed. Reconnecting.")
                serverConnection.apply {
                    disconnect()
                    connect(appPreferenceManager.getWebSocketUrl())
                }
            }

            AppPreferenceManager.KEY_GEOFENCE_ENABLED -> {
                if (appPreferenceManager.getGeofenceEnabled()) {
                    Log.i("PreferenceObserver", "Geofence enabled. Starting monitoring.")
                    geofenceController.startMonitoring()
                } else {
                    Log.i("PreferenceObserver", "Geofence disabled. Stopping monitoring.")
                    geofenceController.stopMonitoring()
                }
            }

            AppPreferenceManager.KEY_GEOFENCE_RADIUS,
            AppPreferenceManager.KEY_GEOFENCE_CENTER_LAT,
            AppPreferenceManager.KEY_GEOFENCE_CENTER_LON,
            AppPreferenceManager.KEY_POLLING_FREQUENCY -> {
                Log.i("PreferenceObserver", "Geofence configuration changed. Reinitializing geofence.")
                geofenceController.initializeGeofence()
            }

            else -> Log.w("PreferenceObserver", "Unknown preference key changed: $key")
        }
    }
}
