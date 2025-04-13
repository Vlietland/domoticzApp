package com.wiconic.domoticzapp.connectivity

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.wiconic.domoticzapp.model.Geofence

class LocationConnector(private val context: Context) {
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private const val TAG = "LocationConnector"

    fun getLastKnownLocation(): Location? {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "Location permission not granted")
            return null
        }
        var lastKnownLocation: Location? = null
        val providers = locationManager.getProviders(true)
        Log.d(TAG, "Providers available: $providers")
        for (provider in providers) {
            val location = locationManager.getLastKnownLocation(provider)
            Log.d(TAG, "Provider: $provider, Location: $location")
            if (location != null) {
                if (lastKnownLocation == null || location.accuracy < lastKnownLocation.accuracy) {
                    lastKnownLocation = location
                    Log.d(TAG, "Updated best location from provider: $provider")
                }
            }
        }
        Log.d(TAG, "Final selected location: $lastKnownLocation")
        return lastKnownLocation
    }
}
