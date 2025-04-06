package com.wiconic.domoticzapp.model

import android.content.Context
import androidx.preference.PreferenceManager

class AppPreferences(private val context: Context) {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    companion object {
        const val KEY_SERVER_IP_ADDRESS = "server_ip"
        const val KEY_SERVER_PORT = "server_port"
        const val KEY_GEOFENCE_ENABLED = "geofence_enabled"
        const val KEY_GEOFENCE_RADIUS = "geofence_radius"
        const val KEY_GEOFENCE_CENTER_LAT = "geofence_lat"
        const val KEY_GEOFENCE_CENTER_LON = "geofence_lon"
        const val KEY_POLLING_FREQUENCY = "polling_frequency"
        const val KEY_MEASUREMENTS = "measurements_before_trigger"

        const val DEFAULT_SERVER_IP_ADDRESS = "192.168.0.1"
        const val DEFAULT_SERVER_PORT = 8080
        const val DEFAULT_GEOFENCE_RADIUS = 100
        const val DEFAULT_CENTER_LAT = 12.379189
        const val DEFAULT_CENTER_LON = 44.899431
        const val DEFAULT_POLLING_FREQUENCY = 60000L
        const val DEFAULT_MEASUREMENTS_BEFORE_TRIGGER = 3
    }

    fun getServerIpAddress(): String =
        sharedPreferences.getString(KEY_SERVER_IP_ADDRESS, DEFAULT_SERVER_IP_ADDRESS) ?: DEFAULT_SERVER_IP_ADDRESS

    fun setServerIpAddress(value: String) {
        sharedPreferences.edit().putString(KEY_SERVER_IP_ADDRESS, value).apply()
    }

    fun getServerPort(): Int {
        val stringValue = sharedPreferences.getString(KEY_SERVER_PORT, DEFAULT_SERVER_PORT.toString())
        return stringValue?.toIntOrNull() ?: DEFAULT_SERVER_PORT
    }

    fun setServerPort(value: Int) {
        sharedPreferences.edit().putString(KEY_SERVER_PORT, value.toString()).apply()
    }

    fun getGeofenceEnabled(): Boolean =
        sharedPreferences.getBoolean(KEY_GEOFENCE_ENABLED, false)

    fun setGeofenceEnabled(value: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_GEOFENCE_ENABLED, value).apply()
    }

    fun getGeofenceRadius(): Int =
        sharedPreferences.getInt(KEY_GEOFENCE_RADIUS, DEFAULT_GEOFENCE_RADIUS)

    fun setGeofenceRadius(value: Int) {
        sharedPreferences.edit().putInt(KEY_GEOFENCE_RADIUS, value).apply()
    }

    fun getGeofenceCenterLat(): Double {
        val stringValue = sharedPreferences.getString(KEY_GEOFENCE_CENTER_LAT, DEFAULT_CENTER_LAT.toString())
        return stringValue?.toDoubleOrNull() ?: DEFAULT_CENTER_LAT
    }

    fun setGeofenceCenterLat(value: Double) {
        sharedPreferences.edit().putString(KEY_GEOFENCE_CENTER_LAT, value.toString()).apply()
    }

    fun getGeofenceCenterLon(): Double {
        val stringValue = sharedPreferences.getString(KEY_GEOFENCE_CENTER_LON, DEFAULT_CENTER_LON.toString())
        return stringValue?.toDoubleOrNull() ?: DEFAULT_CENTER_LON
    }

    fun setGeofenceCenterLon(value: Double) {
        sharedPreferences.edit().putString(KEY_GEOFENCE_CENTER_LON, value.toString()).apply()
    }

    fun getPollingFrequency(): Long {
        val stringValue = sharedPreferences.getString(KEY_POLLING_FREQUENCY, DEFAULT_POLLING_FREQUENCY.toString())
        return stringValue?.toLongOrNull() ?: DEFAULT_POLLING_FREQUENCY
    }

    fun setPollingFrequency(value: Long) {
        sharedPreferences.edit().putString(KEY_POLLING_FREQUENCY, value.toString()).apply()
    }

    fun getMeasurementsBeforeTrigger(): Int {
        val stringValue = sharedPreferences.getString(KEY_MEASUREMENTS, DEFAULT_MEASUREMENTS_BEFORE_TRIGGER.toString())
        return stringValue?.toIntOrNull() ?: DEFAULT_MEASUREMENTS_BEFORE_TRIGGER
    }

    fun setMeasurementsBeforeTrigger(value: Int) {
        sharedPreferences.edit().putString(KEY_MEASUREMENTS, value.toString()).apply()
    }

    fun getWebSocketUrl(): String =
        "ws://${getServerIpAddress()}:${getServerPort()}/app"
}
