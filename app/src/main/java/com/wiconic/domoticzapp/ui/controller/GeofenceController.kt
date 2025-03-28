package com.wiconic.domoticzapp.ui.controller

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.wiconic.domoticzapp.geofence.DynamicGeofenceManager
import com.wiconic.domoticzapp.settings.GeofencePreferenceManager

class GeofenceController(
    private val context: Context,
    private val geofencePreferenceManager: GeofencePreferenceManager
) {
    private var geofenceManager: DynamicGeofenceManager? = null
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    fun initializeGeofence() {
        updateGeofenceFromPreferences()
        checkLocationPermission()
    }

    fun removeGeofence() {
        geofenceManager?.stopMonitoring()
    }

    private fun initializeGeofenceManager(
        lat: Double,
        lon: Double,
        radius: Float,
        measurements: Int,
        frequency: Long
    ) {
        geofenceManager = DynamicGeofenceManager(context, lat, lon, radius, measurements, frequency)
    }

    private fun updateGeofenceFromPreferences() {
        try {
            geofenceManager?.stopMonitoring()
            initializeGeofenceManager(
                geofencePreferenceManager.getLatitude(),
                geofencePreferenceManager.getLongitude(),
                geofencePreferenceManager.getRadius(),
                geofencePreferenceManager.getMeasurementsBeforeTrigger(),
                geofencePreferenceManager.getPollingFrequency()
            )
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                geofenceManager?.startMonitoring()
            }
        } catch (e: Exception) {
            showToast("Error updating geofence: ${e.message}")
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (context is AppCompatActivity) {
                ActivityCompat.requestPermissions(
                    context, 
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            } else {
                showToast("Cannot request location permission")
            }
        } else {
            geofenceManager?.startMonitoring()
        }
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                geofenceManager?.startMonitoring()
                showToast("Location permission granted, geofence monitoring started")
            } else {
                showToast("Location permission denied, geofence monitoring disabled")
            }
        }
    }

    fun onDestroy() {
        geofenceManager?.stopMonitoring()
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
