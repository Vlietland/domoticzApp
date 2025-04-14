package com.wiconic.domoticzapp.controller

import android.util.Log
import android.widget.ImageView
import com.wiconic.domoticzapp.R
import com.wiconic.domoticzapp.model.AppPreferences
import com.wiconic.domoticzapp.connectivity.LocationConnector
import com.wiconic.domoticzapp.model.Geofence
import kotlinx.coroutines.*

class GeofenceController(
    private val openGate: () -> Unit,
    private val closeGate: () -> Unit,
    private val locationConnector: LocationConnector,
    private val geofence: Geofence,
    private val appPreferences: AppPreferences,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    private enum class LocationState {
        UNAVAILABLE,
        UNRELIABLE,
        RELIABLE
    }

    private var geofenceIconView: ImageView? = null
    private var lastIcon: Int? = null
    private val ICON_INSIDE_GEOFENCE = R.drawable.ic_geofence_within_fence
    private val ICON_OUTSIDE_GEOFENCE = R.drawable.ic_geofence_outside_fence
    private val ICON_LOCATION_UNAVAILABLE = R.drawable.ic_geofence_location_unavailable
    private val ICON_LOCATION_UNRELIABLE = R.drawable.ic_geofence_location_unreliable
    private val SHORT_POLLING_DELAY = 1000
    private var pollingJob: Job? = null
    private var locationState: LocationState = LocationState.UNAVAILABLE
    private val TAG = "GeofenceController"

    fun setGeofenceIconView(iconView: ImageView) {
        geofenceIconView = iconView
        updateGeofenceIcon()
        if (appPreferences.getGeofenceEnabled()) startGeofenceMonitoring()
    }

    fun restartGeofenceMonitoring() {
        stopGeofenceMonitoring()
        startGeofenceMonitoring()
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
                val threshold = appPreferences.getAccuracyThreshold()
                if (location == null) {
                    locationState = LocationState.UNAVAILABLE
                    Log.w(TAG, "Location is null")
                    geofence.updateLocation(null, null)
                } else if (location.accuracy > threshold) {
                    locationState = LocationState.UNRELIABLE
                    geofence.updateLocation(null, null)                    
                    Log.d(TAG, "Location: lat=${location.latitude}, lon=${location.longitude} Accuracy not ok: ${location.accuracy} > threshold: $threshold")                    
                } else {
                    locationState = LocationState.RELIABLE
                    Log.d(TAG, "Location: lat=${location.latitude}, lon=${location.longitude} Accuracy ok: ${location.accuracy} < threshold: $threshold")
                    geofence.updateLocation(location.latitude, location.longitude)
                    if (geofence.getFenceTripped()) {
                        processGateCommand()
                        geofence.resetFenceTripped()
                    }
                }
                updateGeofenceIcon()                
                delay(geofence.newDelayMilliseconds())
            }
        }
    }

    private fun stopPollingLocation() {
        pollingJob?.cancel()
    }

    private fun processGateCommand() {
        if (geofence.getIsWithinGeofence() && appPreferences.getGeofenceTriggerOpenEnabled()) {
            Log.i(TAG, "Sending gate open command")
            openGate()
        } else if (!geofence.getIsWithinGeofence() && appPreferences.getGeofenceTriggerCloseEnabled()) {
            Log.i(TAG, "Sending gate close command")
            closeGate()
        }
    }

    private fun updateGeofenceIcon() {
        val icon = when (locationState) {
            LocationState.UNAVAILABLE -> ICON_LOCATION_UNAVAILABLE
            LocationState.UNRELIABLE -> ICON_LOCATION_UNRELIABLE
            LocationState.RELIABLE -> if (geofence.getIsWithinGeofence()) ICON_INSIDE_GEOFENCE else ICON_OUTSIDE_GEOFENCE
        }
        if (lastIcon != icon) {
            Log.d(TAG, "Updating geofence icon to: $icon")
            val visibility = if (appPreferences.getGeofenceEnabled()) ImageView.VISIBLE else ImageView.GONE
            geofenceIconView?.setImageResource(icon)
            geofenceIconView?.visibility = visibility
            lastIcon = icon
        }
    }
}
