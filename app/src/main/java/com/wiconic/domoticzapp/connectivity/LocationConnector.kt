package com.wiconic.domoticzapp.connectivity

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat

class LocationConnector(private val context: Context) {
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private val TAG = "LocationConnector"

    fun startLocationUpdates(listener: LocationListener, minTimeMs: Long, minDistanceM: Float) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "Location permission not granted, cannot request updates")
            return
        }

        val providers = locationManager.getProviders(true)
        Log.i(TAG, "Requesting location updates from providers: $providers")

        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            Log.i(TAG, "Requesting updates from GPS_PROVIDER (minTime=${minTimeMs}ms, minDistance=${minDistanceM}m)")
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTimeMs, minDistanceM, listener, Looper.getMainLooper())
        }
        if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
             Log.i(TAG, "Requesting updates from NETWORK_PROVIDER (minTime=${minTimeMs}ms, minDistance=${minDistanceM}m)")
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTimeMs, minDistanceM, listener, Looper.getMainLooper())
        }
        if (!providers.contains(LocationManager.GPS_PROVIDER) && !providers.contains(LocationManager.NETWORK_PROVIDER)) {
             Log.w(TAG, "No suitable location providers (GPS or Network) available for updates.")
        }
    }

    fun stopLocationUpdates(listener: LocationListener) {
        Log.d(TAG, "Stopping location updates")
        locationManager.removeUpdates(listener)
    }
}
