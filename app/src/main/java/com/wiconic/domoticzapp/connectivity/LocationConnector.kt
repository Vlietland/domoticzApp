package com.wiconic.domoticzapp.connectivity

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.wiconic.domoticzapp.model.Geofence

class LocationConnector(private val context: Context) {
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private val TAG = "LocationConnector"
    val MAX_AGE_MS = 6000    

fun getLastKnownLocation(): Location? {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        Log.w(TAG, "Location permission not granted")
        return null
    }
    var lastKnownLocation: Location? = null
    val providers = locationManager.getProviders(true)
    Log.d(TAG, "The following providers are available: $providers")
    for (provider in providers) {
        val location = locationManager.getLastKnownLocation(provider)
        if (location != null) {
            val age = System.currentTimeMillis() - location.time
            Log.d(TAG, "Provider: $provider, Location${location}:\n Lat=${location.latitude}, Lon=${location.longitude}, Accuracy=${location.accuracy}, Age=$age ms")
            if (age <= MAX_AGE_MS) {
                if (lastKnownLocation == null || location.accuracy < lastKnownLocation.accuracy) {
                    lastKnownLocation = location
                    Log.d(TAG, "Updated best recent location from provider: $provider")
                }
            } else {
                Log.d(TAG, "Discarded old location from provider: $provider")
            }
        }
    }
    if (lastKnownLocation == null) {
        when {
            providers.contains(LocationManager.NETWORK_PROVIDER) -> {
                locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, singleUpdateListener, Looper.getMainLooper())
                Log.d(TAG, "Requested single update from NETWORK provider")
            }
            providers.contains(LocationManager.GPS_PROVIDER) -> {
                locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, singleUpdateListener, Looper.getMainLooper())
                Log.d(TAG, "Requested single update from GPS provider")
            }
            else -> {
                Log.w(TAG, "No usable location providers available for single update")
            }
        }
    }
    Log.d(TAG, "Final selected location: $lastKnownLocation")
    return lastKnownLocation
}

    private val singleUpdateListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            Log.d(TAG, "Received single update: $location")
        }
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }
}
