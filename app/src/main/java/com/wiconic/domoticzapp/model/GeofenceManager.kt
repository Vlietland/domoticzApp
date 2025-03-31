package com.wiconic.domoticzapp.model

import com.wiconic.domoticzapp.ui.controller.GeofenceController

class GeofenceModel(
    private val geofenceCenterLat: Double,
    private val geofenceCenterLon: Double,
    private val geofenceRadius: Int,
    private val measurementsBeforeTrigger: Int,
    private val controller: GeofenceController  // Controller passed directly
) {

    var isTriggered = false
        private set

    var hasLeftArea = true
        private set

    private var consecutiveInAreaCount = 0

    fun updateLocation(lat: Double, lon: Double) {
        val distance = calculateDistance(lat, lon, geofenceCenterLat, geofenceCenterLon)
        val isInArea = distance <= geofenceRadius

        if (isInArea) {
            if (++consecutiveInAreaCount >= measurementsBeforeTrigger && !isTriggered && hasLeftArea) {
                isTriggered = true
                hasLeftArea = false
                controller.onGeofenceTriggered() // Directly calling a function in the controller
            }
        } else {
            consecutiveInAreaCount = 0
            if (isTriggered) hasLeftArea = true
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        android.location.Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }
}
