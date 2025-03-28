package com.wiconic.domoticzapp

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

class DomoticzApp : Application() {
    var isGooglePlayServicesAvailable = false
        private set

    var isNetworkAvailable = false
        private set

    override fun onCreate() {
        super.onCreate()
        checkGooglePlayServices()
        checkNetworkAvailability()
    }

    private fun checkGooglePlayServices() {
        try {
            val availability = GoogleApiAvailability.getInstance()
            val resultCode = availability.isGooglePlayServicesAvailable(this)
            isGooglePlayServicesAvailable = resultCode == ConnectionResult.SUCCESS
            
            if (!isGooglePlayServicesAvailable) {
                Log.w("DomoticzApp", "Google Play Services not available. Some features will be disabled.")
            }
        } catch (e: Exception) {
            Log.e("DomoticzApp", "Error checking Google Play Services: ${e.message}")
            isGooglePlayServicesAvailable = false
        }
    }

    private fun checkNetworkAvailability() {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        
        isNetworkAvailable = capabilities?.let {
            it.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            (it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || 
             it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
        } ?: false

        Log.i("DomoticzApp", "Network available: $isNetworkAvailable")
    }
}
