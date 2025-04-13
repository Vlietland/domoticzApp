package com.wiconic.domoticzapp.model

import com.wiconic.domoticzapp.model.AppPreferences
import android.util.Log


class Geofence(
    private val appPreferences: AppPreferences,
) {
    private var geofenceCenterLat: Double = appPreferences.getGeofenceCenterLat()
    private var geofenceCenterLon: Double = appPreferences.getGeofenceCenterLon()
    private var geofenceRadius: Int = appPreferences.getGeofenceRadius()
    private var measurementsBeforeTrigger: Int = appPreferences.getMeasurementsBeforeTrigger()
    private var lastDistance: Float = Float.MAX_VALUE
    private var fenceTripped: Boolean = false
    private var lastWithinGeofence: Boolean = true
    private var consecutiveInAreaCount = 0
    private var newDelay: Long = 5000
    private var testDistanceCounter = 0     // testing  
    private val TAG = "Geofence"      

    fun updateGeofence() {
        geofenceCenterLat = appPreferences.getGeofenceCenterLat()
        geofenceCenterLon = appPreferences.getGeofenceCenterLon()
        geofenceRadius = appPreferences.getGeofenceRadius()
        measurementsBeforeTrigger = appPreferences.getMeasurementsBeforeTrigger()
    }

    fun updateLocation(lat: Double, lon: Double) {
        lastDistance = calculateDistance(lat, lon, geofenceCenterLat, geofenceCenterLon)
        val withinGeofence = lastDistance <= geofenceRadius.toFloat()
        if (lastWithinGeofence != withinGeofence) {
            if (++consecutiveInAreaCount >= measurementsBeforeTrigger) {
                fenceTripped = true
                Log.d(TAG, "Fence tripped: $fenceTripped")                
                lastWithinGeofence = withinGeofence
            }
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

        val speedMetersPerSecond = if (distanceMeters < NEAR_DISTANCE_THRESHOLD_METERS)
            SPEED_LIMIT_NEAR_KMH / SPEED_CONVERSION_FACTOR
        else SPEED_LIMIT_FAR_KMH / SPEED_CONVERSION_FACTOR
        val delayMs = (distanceMeters / speedMetersPerSecond) * 1000f
        Log.d(TAG, "Calculated delay for the next location check: $delayMs")
        return delayMs.toLong().coerceIn(MIN_POLLING_DELAY_MS, MAX_POLLING_DELAY_MS)
    }

    fun newDelay() = newDelay
    fun getIsWithinGeofence() = lastWithinGeofence
    fun getFenceStatusChanged() = fenceTripped
    fun resetFenceStatusChanged() {fenceTripped = false}

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        testDistanceCounter++
        val cyclePosition = (testDistanceCounter - 1) % 10
        val result = if (cyclePosition < 5) 500f else 0f
        Log.d(TAG, "Distance array: $result")
        return result
        //val results = FloatArray(1)
        //android.location.Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        //Log.d(TAG, "Distance array: ${results.contentToString()}")
        //return results[0]
    }
}
