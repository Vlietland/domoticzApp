package com.wiconic.domoticzapp

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log

class DomoticzApp : Application() {

    var isNetworkAvailable = false
        private set

    override fun onCreate() {
        super.onCreate()
        checkNetworkAvailability()
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
