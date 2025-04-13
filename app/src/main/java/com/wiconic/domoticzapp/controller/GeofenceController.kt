package com.wiconic.domoticzapp.controller

import android.util.Log
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.wiconic.domoticzapp.R
import com.wiconic.domoticzapp.model.AppPreferences
import com.wiconic.domoticzapp.connectivity.LocationConnector
import com.wiconic.domoticzapp.model.Geofence

import kotlinx.coroutines.*

class GeofenceController(
    private val openGate: () -> Unit,
    private val locationConnector: LocationConnector,
    private val geofence: Geofence,
    private val appPreferences: AppPreferences,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    private var geofenceIcon: ImageView? = null
    private val TAG = "GeofenceController"
    private val ICON_INSIDE_GEOFENCE = R.drawable.ic_geofence_within_fence
    private val ICON_OUTSIDE_GEOFENCE = R.drawable.ic_geofence_outside_fence
    private val ICON_LOCATION_UNAVAILABLE = R.drawable.ic_geofence_location_unavailable
    private var pollingJob: Job? = null
    private var hasLocation: Boolean = false

    fun setGeofenceIcon(icon: ImageView) {
        geofenceIcon = icon
        updateGeofenceIcon()
        if (appPreferences.getGeofenceEnabled()) startGeofenceMonitoring()
    }

    fun startGeofenceMonitoring() {
        Log.i(TAG, "Geofence monitoring started.")
        startPollingLocation()
        updateGeofenceIcon()
    }

    fun stopGeofenceMonitoring() {
        Log.i(TAG, "Geofence monitoring stopped.")
        stopPollingLocation()
        updateGeofenceIcon()
    }

    private fun startPollingLocation() {
        pollingJob = coroutineScope.launch {
            while (isActive) {
                val location = locationConnector.getLastKnownLocation()
                var hasLocation = (location != null)
                Log.i(TAG, "Geofence location received: $location")                
                location?.let {
                    geofence.updateLocation(it.latitude, it.longitude)
                    updateGeofenceIcon()
                    if (geofence.isWithinGeofence()) openGate()
                }
                delay(5000)
            }
        }
    }

    private fun stopPollingLocation() {
        pollingJob?.cancel()
    }

    private fun updateGeofenceIcon() {
        val icon = when {
            !hasLocation -> ICON_LOCATION_UNAVAILABLE
            geofence.isTriggered -> ICON_INSIDE_GEOFENCE
            else -> ICON_OUTSIDE_GEOFENCE
        }
        val visibility = if (appPreferences.getGeofenceEnabled()) ImageView.VISIBLE else ImageView.GONE
        geofenceIcon?.setImageResource(icon)
        geofenceIcon?.visibility = visibility
    }
}
