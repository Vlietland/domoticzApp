package com.wiconic.domoticzapp.controller

import android.content.Context
import androidx.preference.PreferenceManager
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.wiconic.domoticzapp.model.AppPreferences

class PreferenceObserver(
    private val context: Context,
    private val appPreferences: AppPreferences,
    private val initializeConnection: () -> Unit,    
    private val initializeGeofence: () -> Unit,
    private val stopGeofence: () -> Unit,
) : SharedPreferences.OnSharedPreferenceChangeListener, DefaultLifecycleObserver {

    private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private var isRegistered = false    
    private val TAG = "PreferenceObserver"

    init {
        Log.i(TAG, "PreferenceObserver instance created.")
    }

    override fun onCreate(owner: LifecycleOwner) {
        register()
    }

    private fun register() {
        if (!isRegistered) {
            sharedPreferences.registerOnSharedPreferenceChangeListener(this)
            isRegistered = true
            Log.i(TAG, "PreferenceObserver registered.")
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            AppPreferences.KEY_SERVER_IP_ADDRESS,
            AppPreferences.KEY_SERVER_PORT -> {
                Log.i(TAG, "Websocket connection settings changed. Reconnecting.")
                initializeConnection()
            }

            AppPreferences.KEY_GEOFENCE_ENABLED -> {
                if (appPreferences.getGeofenceEnabled()) {
                    initializeGeofence()                    
                    Log.i(TAG, "Geofence enabled. Starting monitoring.")
                } else {
                    Log.i(TAG, "Geofence disabled. Stopping monitoring.")
                    stopGeofence()
                }
            }

            AppPreferences.KEY_GEOFENCE_RADIUS,
            AppPreferences.KEY_GEOFENCE_CENTER_LAT,
            AppPreferences.KEY_GEOFENCE_CENTER_LON,
            AppPreferences.KEY_MIN_POLLING_DELAY,
            AppPreferences.KEY_MAX_POLLING_DELAY -> {
                Log.i(TAG, "Geofence configuration changed. Reinitializing geofence.")
                initializeGeofence()
            }

            else -> Log.w(TAG, "Unknown preference key changed: $key")
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        unregister()
    }

    fun unregister() {
        if (isRegistered) {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
            isRegistered = false
            Log.i(TAG, "PreferenceObserver unregistered.")
        }
    }
}
