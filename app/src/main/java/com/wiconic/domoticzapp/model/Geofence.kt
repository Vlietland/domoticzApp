package com.wiconic.domoticzapp.model
import com.wiconic.domoticzapp.model.AppPreferences

class Geofence(
    private val appPreferences: AppPreferences,
) {
    private var geofenceCenterLat: Double = appPreferences.getGeofenceCenterLat()
    private var geofenceCenterLon: Double = appPreferences.getGeofenceCenterLon()
    private var geofenceRadius: Int = appPreferences.getGeofenceRadius()
    private var measurementsBeforeTrigger: Int = appPreferences.getMeasurementsBeforeTrigger()
    private var lastDistance: Float = Float.MAX_VALUE
    private var fenceStatusChanged: Boolean = false
    private var withinGeofence: Boolean = true
    private var testDistanceCounter = 0     // testing

    private var consecutiveInAreaCount = 0

    fun updateGeofence() {
        geofenceCenterLat = appPreferences.getGeofenceCenterLat()
        geofenceCenterLon = appPreferences.getGeofenceCenterLon()
        geofenceRadius = appPreferences.getGeofenceRadius()
        measurementsBeforeTrigger = appPreferences.getMeasurementsBeforeTrigger()
    }

    fun updateLocation(lat: Double, lon: Double) {
        lastDistance = calculateDistance(lat, lon, geofenceCenterLat, geofenceCenterLon)
        val checkInArea = lastDistance <= geofenceRadius.toFloat()
        if (withinGeofence != checkInArea) {
            if (++consecutiveInAreaCount >= measurementsBeforeTrigger) {
                fenceStatusChanged = true
                withinGeofence = checkInArea
            }
        }
        else consecutiveInAreaCount = 0
    }

    fun getDistanceFromGeofence() = lastDistance
    fun getIsWithinGeofence() = withinGeofence
    fun getFenceStatusChanged() = fenceStatusChanged
    fun resetFenceStatusChanged() {fenceStatusChanged = false}

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        //testDistanceCounter++
        //val cyclePosition = (testDistanceCounter - 1) % 10
        //return if (cyclePosition < 5) 500f else 0f
        val results = FloatArray(1)
        android.location.Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }
}
