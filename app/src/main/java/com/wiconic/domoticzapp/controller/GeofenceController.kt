package com.wiconic.domoticzapp.controller

import android.content.Context
import android.util.Log
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.wiconic.domoticzapp.R
import com.wiconic.domoticzapp.model.Geofence
import com.wiconic.domoticzapp.model.AppPreferences

class GeofenceController(
    private val context: Context,
    private val openGate: () -> Unit,
    private val appPreferences: AppPreferences,
    private val geofenceIcon: ImageView
) {
    private var isCurrentlyWithinGeofence = false
    private val TAG = "GeofenceController"
    private val ICON_INSIDE_GEOFENCE = R.drawable.ic_baseline_location_on
    private val ICON_OUTSIDE_GEOFENCE = R.drawable.ic_baseline_location_off

    init {
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

    fun onIsWithinGeofenceCallback(inGeofence: Boolean) {
        Log.i(TAG, "Handling geofence state change in the controller.")
        isCurrentlyWithinGeofence = inGeofence
        updateGeofenceIcon()
        if (inGeofence) openGate()
    }

    private fun updateGeofenceIcon() {
        val icon = if (isCurrentlyWithinGeofence) ICON_INSIDE_GEOFENCE else ICON_OUTSIDE_GEOFENCE
        val visibility = if (appPreferences.getGeofenceEnabled()) ImageView.VISIBLE else ImageView.GONE

        geofenceIcon.setImageResource(icon)
        geofenceIcon.visibility = visibility
    }
}
