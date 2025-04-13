package com.wiconic.domoticzapp.model

import com.wiconic.domoticzapp.model.AppPreferences
import android.util.Log


class Geofence(
    private val appPreferences: AppPreferences,
) {
    private var lastDistance: Float = Float.MAX_VALUE
    private var fenceTripped: Boolean = false
    private var lastWithinGeofence: Boolean = true
    private var consecutiveInAreaCount = 0
    private var newDelay: Long = 5000
    private var testDistanceCounter = 0     // testing  
    private val TAG = "Geofence"      

    fun updateLocation(lat: Double, lon: Double) {
        lastDistance = calculateDistance(lat, lon, 
                                         appPreferences.getGeofenceCenterLat(),
                                         appPreferences.getGeofenceCenterLon()
                                         )
        Log.i(TAG, "Geofence location updated")         
        val withinGeofence = lastDistance <= appPreferences.getGeofenceRadius().toFloat()
        Log.d(TAG, "Within geofence: $withinGeofence")        
        if (lastWithinGeofence != withinGeofence) {
            fenceTripped = true
            Log.d(TAG, "Fence tripped: $fenceTripped")                
            lastWithinGeofence = withinGeofence
        }
        else consecutiveInAreaCount = 0
        newDelay = calculateNewDelay(lastDistance)        
    }

    private fun calculateNewDelay(distanceMeters: Float): Long {
        val MIN_POLLING_DELAY_MS = appPreferences.getMinPollingDelay() * 1000
        val MAX_POLLING_DELAY_MS = appPreferences.getMaxPollingDelay() * 1000
        val SPEED_LIMIT_NEAR_KMH = 50
        val SPEED_LIMIT_FAR_KMH = 120
        val SPEED_CONVERSION_FACTOR = 3.6f
        val NEAR_DISTANCE_THRESHOLD_METERS = 5000f
        val POLL_DELAY_WITHIN_GEOFENCE_MS = 600000L //600 seconds

        val speedMetersPerSecond = if (distanceMeters < NEAR_DISTANCE_THRESHOLD_METERS)
             SPEED_LIMIT_NEAR_KMH / SPEED_CONVERSION_FACTOR
        else SPEED_LIMIT_FAR_KMH  / SPEED_CONVERSION_FACTOR
        val delayMs = (distanceMeters / speedMetersPerSecond) * 1000f
        if (lastWithinGeofence) {
            Log.d(TAG, "Calculated delay: $delayMs; Selected delay in geofence: $POLL_DELAY_WITHIN_GEOFENCE_MS")
            return POLL_DELAY_WITHIN_GEOFENCE_MS
        }
        else {
            val selectedDelay = delayMs.toLong().coerceIn(MIN_POLLING_DELAY_MS, MAX_POLLING_DELAY_MS)
            Log.d(TAG, "Calculated delay: $delayMs; Selected delay outside geofence: $selectedDelay")
            return delayMs.toLong().coerceIn(MIN_POLLING_DELAY_MS, MAX_POLLING_DELAY_MS)
        }
    }

    fun newDelay() = newDelay
    fun getIsWithinGeofence() = lastWithinGeofence
    fun getFenceTripped() = fenceTripped
    fun resetFenceTripped() {fenceTripped = false}

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        android.location.Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        Log.d(TAG, "Distance array: ${results.contentToString()}")
        return results[0]
    }
}
