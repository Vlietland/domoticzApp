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
    private val closeGate: () -> Unit,
    private val locationConnector: LocationConnector,
    private val geofence: Geofence,
    private val appPreferences: AppPreferences,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    private var geofenceIcon: ImageView? = null
    private val ICON_INSIDE_GEOFENCE = R.drawable.ic_geofence_within_fence
    private val ICON_OUTSIDE_GEOFENCE = R.drawable.ic_geofence_outside_fence
    private val ICON_LOCATION_UNAVAILABLE = R.drawable.ic_geofence_location_unavailable
    private val SHORT_POLLING_DELAY = 1000L
    private var pollingJob: Job? = null
    private var hasLocation: Boolean = false
    private val TAG = "GeofenceController"

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
                val sampleCount = appPreferences.getMeasurementsBeforeTrigger()
                val latitudes = mutableListOf<Double>()
                val longitudes = mutableListOf<Double>()
                repeat(sampleCount) {
                    val location = locationConnector.getLastKnownLocation()
                    if (location != null) {
                        latitudes.add(location.latitude)
                        longitudes.add(location.longitude)
                    }
                    delay(SHORT_POLLING_DELAY)
                }
                hasLocation = latitudes.isNotEmpty() && longitudes.isNotEmpty()
                if (hasLocation) {
                    val avgLat = latitudes.average()
                    val avgLon = longitudes.average()
                    Log.i(TAG, "Geofence averaged location: lat=$avgLat, lon=$avgLon")
                    geofence.updateLocation(avgLat, avgLon)
                    updateGeofenceIcon()                    
                    if (geofence.getFenceTripped()) {
                        processGateCommand()
                        geofence.resetFenceTripped()
                    }
                } else {
                    Log.w(TAG, "No valid location samples collected")
                }

                delay(geofence.newDelay())
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
        val icon = when {
            !hasLocation -> ICON_LOCATION_UNAVAILABLE
            geofence.getIsWithinGeofence() -> ICON_INSIDE_GEOFENCE
            else -> ICON_OUTSIDE_GEOFENCE
        }
        Log.d(TAG, "Updating geofence icon to: $icon")
        val visibility = if (appPreferences.getGeofenceEnabled()) ImageView.VISIBLE else ImageView.GONE
        geofenceIcon?.setImageResource(icon)
        geofenceIcon?.visibility = visibility
    }
}
