package com.wiconic.domoticzapp.model

import com.wiconic.domoticzapp.model.AppPreferences
import android.util.Log


class Geofence(
    private val appPreferences: AppPreferences,
) {
    private var fenceTripped: Boolean = false
    private var lastWithinGeofence: Boolean = true
    val DEFAULT_DELAY = 60L    
    private var newDelay: Long = DEFAULT_DELAY
    private val TAG = "Geofence"      

    fun updateLocation(lat: Double?, lon: Double?) {
        var distance: Float? = null
        if (lat != null && lon != null) {
            distance = calculateDistance(lat, lon, appPreferences.getGeofenceCenterLat(), appPreferences.getGeofenceCenterLon())
            Log.i(TAG, "Geofence location calculated")         
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
        newDelay = calculateNewDelay(distance)        
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        android.location.Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        Log.d(TAG, "Distance array: ${results.contentToString()}")
        return results[0]
    }

    private fun calculateNewDelay(distanceMeters: Float?): Long {
        if (distanceMeters == null) {
            Log.d(TAG, "Unknown distance, default delay selected: $DEFAULT_DELAY in seconds")        
            return DEFAULT_DELAY
        }
        val MIN_POLLING_DELAY = appPreferences.getMinPollingDelay()
        val MAX_POLLING_DELAY = appPreferences.getMaxPollingDelay()
        val SPEED_LIMIT_NEAR_KMH = 50
        val SPEED_LIMIT_FAR_KMH = 120
        val SPEED_CONVERSION_FACTOR = 3.6f
        val NEAR_DISTANCE = 5000f //5km

        val speedMetersPerSecond = if (distanceMeters < NEAR_DISTANCE)
             SPEED_LIMIT_NEAR_KMH / SPEED_CONVERSION_FACTOR
        else SPEED_LIMIT_FAR_KMH  / SPEED_CONVERSION_FACTOR
        val delay = (distanceMeters / speedMetersPerSecond)
        if (lastWithinGeofence) {
            Log.d(TAG, "Device is within geofence, default delay selected: $DEFAULT_DELAY in seconds")
            return DEFAULT_DELAY
        } else {
            val selectedDelay = delay.toLong().coerceIn(MIN_POLLING_DELAY, MAX_POLLING_DELAY)
            Log.d(TAG, "Calculated delay: $delay; Selected delay outside geofence: $selectedDelay in seconds")
            return delay.toLong().coerceIn(MIN_POLLING_DELAY, MAX_POLLING_DELAY)
        }
    }

    fun newDelayMilliseconds() = newDelay * 1000
    fun getIsWithinGeofence() = lastWithinGeofence
    fun getFenceTripped() = fenceTripped
    fun resetFenceTripped() {fenceTripped = false}
}
