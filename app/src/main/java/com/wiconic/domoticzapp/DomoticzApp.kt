package com.wiconic.domoticzapp

import android.app.Application
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

class DomoticzApp : Application() {
    var isGooglePlayServicesAvailable = false
        private set

    override fun onCreate() {
        super.onCreate()
        checkGooglePlayServices()
    }

    private fun checkGooglePlayServices() {
        try {
            val availability = GoogleApiAvailability.getInstance()
            val resultCode = availability.isGooglePlayServicesAvailable(this)
            isGooglePlayServicesAvailable = resultCode == ConnectionResult.SUCCESS
            
            if (!isGooglePlayServicesAvailable) {
                Log.i("DomoticzApp", "Google Play Services not available. Some features will be disabled.")
            }
        } catch (e: Exception) {
            Log.e("DomoticzApp", "Error checking Google Play Services: ${e.message}")
            isGooglePlayServicesAvailable = false
        }
    }
}
