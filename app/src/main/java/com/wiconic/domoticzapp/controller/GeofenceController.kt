package com.wiconic.domoticzapp.controller

import android.util.Log
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.wiconic.domoticzapp.R
import com.wiconic.domoticzapp.model.AppPreferences
import com.wiconic.domoticzapp.connectivity.LocationConnector
import com.wiconic.domoticzapp.model.Geofence

class GeofenceController(
    private val openGate: () -> Unit,
    private val locationConnector: LocationConnector,
    private val geofence: Geofence,
    private val appPreferences: AppPreferences
) {
    private var isCurrentlyWithinGeofence = false
    private var geofenceIcon: ImageView? = null
    private val TAG = "GeofenceController"
    private val ICON_INSIDE_GEOFENCE = R.drawable.ic_baseline_location_on
    private val ICON_OUTSIDE_GEOFENCE = R.drawable.ic_baseline_location_off

    fun setGeofenceIcon(icon: ImageView) {
        geofenceIcon = icon
        updateGeofenceIcon()
    }

    fun startGeofenceMonitoring() {
        Log.i(TAG, "Geofence monitoring started.")
        updateGeofenceIcon()
    }

    fun stopGeofenceMonitoring() {
        Log.i(TAG, "Geofence monitoring stopped.")
        updateGeofenceIcon()
    }
    
    fun onLocationUpdateCallBack() {
        Log.d(TAG, "Location .")

    }

    fun onIsWithinGeofenceCallback(inGeofence: Boolean) {
        Log.i(TAG, "Handling geofence state change in the controller.")
        isCurrentlyWithinGeofence = inGeofence
        updateGeofenceIcon()
        if (inGeofence) openGate()
    }

    private fun updateGeofenceIcon() {
        if (geofenceIcon == null) return
        val iconResource = if (isCurrentlyWithinGeofence) ICON_INSIDE_GEOFENCE else ICON_OUTSIDE_GEOFENCE
        val visibility = if (appPreferences.getGeofenceEnabled()) ImageView.VISIBLE else ImageView.GONE
        geofenceIcon?.setImageResource(iconResource)
        geofenceIcon?.visibility = visibility
    }
}
