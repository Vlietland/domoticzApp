package com.wiconic.domoticzapp.model

import com.wiconic.domoticzapp.model.AppPreferences
import android.util.Log


class Geofence(
    private val appPreferences: AppPreferences
) {
    private var fenceTripped: Boolean = false
    private var lastWithinGeofence: Boolean = true
    private val TAG = "Geofence"

    fun updateLocation(lat: Double?, lon: Double?) {
        //var distance: Float? = null
        if (lat != null && lon != null) {
            val distance = calculateDistance(lat, lon, appPreferences.getGeofenceCenterLat(), appPreferences.getGeofenceCenterLon())
            Log.i(TAG, "Geofence location calculated, distance: $distance")
            val withinGeofence = distance <= appPreferences.getGeofenceRadius().toFloat()
            Log.d(TAG, "Within geofence: $withinGeofence")
            if (lastWithinGeofence != withinGeofence) {
                fenceTripped = true
                Log.d(TAG, "Fence tripped: $fenceTripped")
                lastWithinGeofence = withinGeofence
            }
        } else {
            Log.w(TAG, "Null location input, skipping geofence check")
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        android.location.Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        Log.d(TAG, "Calculated distance: ${results[0]}")
        return results[0]
    }

    fun getIsWithinGeofence() = lastWithinGeofence
    fun getFenceTripped() = fenceTripped
    fun resetFenceTripped() { fenceTripped = false }
}
