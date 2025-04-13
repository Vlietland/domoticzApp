package com.wiconic.domoticzapp.model
import com.wiconic.domoticzapp.model.AppPreferences

class Geofence(
    private val appPreferences: AppPreferences,
) {
    private var geofenceCenterLat: Double = appPreferences.getGeofenceCenterLat()
    private var geofenceCenterLon: Double = appPreferences.getGeofenceCenterLon()
    private var geofenceRadius: Int = appPreferences.getGeofenceRadius()
    private var measurementsBeforeTrigger: Int = appPreferences.getMeasurementsBeforeTrigger()
    private var onGeofenceStateChangeCallback: ((Boolean) -> Unit)? = null
    private var lastDistance: Float = Float.MAX_VALUE

    var isTriggered = false
        private set

    var hasLeftArea = true
        private set

    private var consecutiveInAreaCount = 0

    fun updateGeofence() {
        geofenceCenterLat = appPreferences.getGeofenceCenterLat()
        geofenceCenterLon = appPreferences.getGeofenceCenterLon()
        geofenceRadius = appPreferences.getGeofenceRadius()
        measurementsBeforeTrigger = appPreferences.getMeasurementsBeforeTrigger()
    }

    fun updateLocation(lat: Double, lon: Double) {
        lastDistance = calculateDistance(lat, lon, geofenceCenterLat, geofenceCenterLon)
        val isInArea = lastDistance <= geofenceRadius.toFloat()

        if (isInArea) {
            if (++consecutiveInAreaCount >= measurementsBeforeTrigger && !isTriggered && hasLeftArea) {
                isTriggered = true
                hasLeftArea = false
                onGeofenceStateChangeCallback?.invoke(true)
            }
        } else {
            consecutiveInAreaCount = 0
            if (isTriggered) hasLeftArea = true
            onGeofenceStateChangeCallback?.invoke(false)
        }
    }

    fun getDistanceFromGeofence(): Float {
        return lastDistance
    }

    fun isWithinGeofence(): Boolean {
        return isTriggered
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        android.location.Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }
}
