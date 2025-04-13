package com.wiconic.domoticzapp.connectivity

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import com.wiconic.domoticzapp.model.Geofence
import com.wiconic.domoticzapp.connectivity.LocationConnector

class LocationConnector(context: Context, private val geofence: Geofence) {
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            geofence.updateLocation(location.latitude, location.longitude)
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    fun startLocationUpdates() {
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            10000L,
            10f,
            locationListener
        )
    }

    fun stopLocationUpdates() {
        locationManager.removeUpdates(locationListener)
    }
}
