package com.wiconic.domoticzapp.controller

import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import com.wiconic.domoticzapp.R
import com.wiconic.domoticzapp.model.AppPreferences
import com.wiconic.domoticzapp.connectivity.LocationConnector
import com.wiconic.domoticzapp.model.Geofence

class GeofenceController(
    private val openGate: () -> Unit,
    private val closeGate: () -> Unit,
    private val locationConnector: LocationConnector,
    private val geofence: Geofence,
    private val appPreferences: AppPreferences
) : LocationListener {
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
    private var locationState: LocationState = LocationState.UNAVAILABLE
    private val TAG = "GeofenceController"

    init {
        if (appPreferences.getGeofenceEnabled()) startGeofenceMonitoring()
    }

    fun setGeofenceIconView(iconView: ImageView) {
        geofenceIconView = iconView
        updateGeofenceIcon(true)
    }

    fun restartGeofenceMonitoring() {
        Log.i(TAG, "Restarting geofence monitoring.")
        stopGeofenceMonitoring()
        if (appPreferences.getGeofenceEnabled()) {
            startGeofenceMonitoring()
        } else {
             Log.i(TAG, "Geofence is disabled, not restarting monitoring.")
             updateGeofenceIcon()
        }
    }

    fun startGeofenceMonitoring() {
        if (!appPreferences.getGeofenceEnabled()) {
            Log.i(TAG, "Attempted to start monitoring, but geofence is disabled.")
            updateGeofenceIcon()
            return
        }
        Log.i(TAG, "Starting geofence monitoring.")
        val minTimeMs = appPreferences.getMinPollingDelay() * 1000L
        val minDistanceM = appPreferences.getMinimumUpdateDistance().toFloat()
        Log.i(TAG, "Using minTimeMs: $minTimeMs, minDistanceM: $minDistanceM from AppPreferences")
        locationConnector.startLocationUpdates(this, minTimeMs, minDistanceM)
        updateGeofenceIcon()
        Exception().printStackTrace()                
    }

    fun stopGeofenceMonitoring() {
        Log.i(TAG, "Stopping geofence monitoring.")
        locationConnector.stopLocationUpdates(this)
        locationState = LocationState.UNAVAILABLE
        updateGeofenceIcon()
    }

    override fun onLocationChanged(location: Location) {
        Log.i(TAG, "Received location update: $location")
        val threshold = appPreferences.getAccuracyThreshold()
        if (location.accuracy > threshold) {
            locationState = LocationState.UNRELIABLE
            Log.w(TAG, "Location accuracy poor: ${location.accuracy} > threshold: $threshold. State set to UNRELIABLE.")
        } else {
            locationState = LocationState.RELIABLE
            Log.i(TAG, "Location accuracy acceptable: ${location.accuracy} <= threshold: $threshold. State set to RELIABLE.")
            geofence.updateLocation(location.latitude, location.longitude)
            if (geofence.getFenceTripped()) {
                processGateCommand()
                geofence.resetFenceTripped()
            }
        }
        updateGeofenceIcon()
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        Log.i(TAG, "Location provider status changed: Provider=$provider, Status=$status")
    }

    override fun onProviderEnabled(provider: String) {
         Log.i(TAG, "Location provider enabled: $provider")
    }

    override fun onProviderDisabled(provider: String) {
        Log.w(TAG, "Location provider disabled: $provider")
    }

    private fun processGateCommand() {
        if (geofence.getIsWithinGeofence() && appPreferences.getGeofenceTriggerOpenEnabled()) {
             Log.i(TAG, "Inside geofence and trigger open enabled. Sending gate open command.")
            openGate()
        } else if (!geofence.getIsWithinGeofence() && appPreferences.getGeofenceTriggerCloseEnabled()) {
             Log.i(TAG, "Outside geofence and trigger close enabled. Sending gate close command.")
            closeGate()
        }
    }

    private fun updateGeofenceIcon(forcedRedraw: Boolean = false) {
        val visibility = if (appPreferences.getGeofenceEnabled()) ImageView.VISIBLE else ImageView.GONE
        val icon = if (!appPreferences.getGeofenceEnabled()) {
             ICON_LOCATION_UNAVAILABLE
        } else {
            when (locationState) {
                LocationState.UNAVAILABLE -> ICON_LOCATION_UNAVAILABLE
                LocationState.UNRELIABLE -> ICON_LOCATION_UNRELIABLE
                LocationState.RELIABLE -> if (geofence.getIsWithinGeofence()) ICON_INSIDE_GEOFENCE else ICON_OUTSIDE_GEOFENCE
            }
        }
        if (lastIcon != icon || geofenceIconView?.visibility != visibility || forcedRedraw) {
            Log.d(TAG, "Updating geofence icon. New Icon: $icon, Visibility: $visibility")
            geofenceIconView?.setImageResource(icon)
            geofenceIconView?.visibility = visibility
            lastIcon = icon
        }
    }
}
