package com.wiconic.domoticzapp.settings

import android.content.Context
import androidx.preference.PreferenceManager

class DomoticzPreferenceManager(val context: Context) {
    private val DEFAULT_SERVER_IP = "192.168.1.1"
    private val DEFAULT_SERVER_PORT = 8080
    private val DEFAULT_GEOFENCE_LAT = "12.3676"
    private val DEFAULT_GEOFENCE_LON = "44.9041"
    private val DEFAULT_GEOFENCE_RADIUS = "100"
    private val DEFAULT_POLLING_FREQUENCY = "60000"
    private val DEFAULT_MEASUREMENTS_BEFORE_TRIGGER = "3"
    
    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    fun getServerIp(): String {
        return prefs.getString("server_ip", DEFAULT_SERVER_IP) ?: DEFAULT_SERVER_IP
    }

    fun getServerPort(): Int {
        return prefs.getString("server_port", DEFAULT_SERVER_PORT.toString())?.toIntOrNull() 
            ?: DEFAULT_SERVER_PORT
    }

    fun getWebSocketUrl(): String {
        return "ws://${getServerIp()}:${getServerPort()}/app"
    }
    
    fun getEnvValue(key: String): String? {
        return when(key) {
            "GEOFENCE_DEFAULT_LAT" -> DEFAULT_GEOFENCE_LAT
            "GEOFENCE_DEFAULT_LON" -> DEFAULT_GEOFENCE_LON
            "GEOFENCE_DEFAULT_RADIUS" -> DEFAULT_GEOFENCE_RADIUS
            "GEOFENCE_DEFAULT_POLLING_FREQUENCY" -> DEFAULT_POLLING_FREQUENCY
            "GEOFENCE_DEFAULT_MEASUREMENTS_BEFORE_TRIGGER" -> DEFAULT_MEASUREMENTS_BEFORE_TRIGGER
            else -> null
        }
    }
}
