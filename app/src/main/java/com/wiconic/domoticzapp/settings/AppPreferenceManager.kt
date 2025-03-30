package com.wiconic.domoticzapp.settings

import android.content.Context
import androidx.preference.PreferenceManager

class AppPreferenceManager(private val context: Context) {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    companion object {
        const val KEY_SERVER_IP_ADDRESS = "server_ip_address"
        const val KEY_SERVER_PORT = "server_port"
        const val KEY_GEOFENCE_ENABLED = "geofence_enabled"
        const val KEY_GEOFENCE_RADIUS = "geofence_radius"
        const val KEY_GEOFENCE_CENTER_LAT = "geofence_center_lat"
        const val KEY_GEOFENCE_CENTER_LON = "geofence_center_lon"
        const val KEY_POLLING_FREQUENCY = "polling_frequency"
        const val KEY_MEASUREMENTS = "measurements_before_trigger"

        const val DEFAULT_SERVER_IP_ADDRESS = "192.168.0.1"
        const val DEFAULT_SERVER_PORT = 5000
        const val DEFAULT_GEOFENCE_RADIUS = 100.0f
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

    fun getServerPort(): Int =
        sharedPreferences.getInt(KEY_SERVER_PORT, DEFAULT_SERVER_PORT)

    fun setServerPort(value: Int) {
        sharedPreferences.edit().putInt(KEY_SERVER_PORT, value).apply()
    }

    fun getGeofenceEnabled(): Boolean =
        sharedPreferences.getBoolean(KEY_GEOFENCE_ENABLED, false)

    fun setGeofenceEnabled(value: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_GEOFENCE_ENABLED, value).apply()
    }

    fun getGeofenceRadius(): Float =
        sharedPreferences.getFloat(KEY_GEOFENCE_RADIUS, DEFAULT_GEOFENCE_RADIUS)

    fun setGeofenceRadius(value: Float) {
        sharedPreferences.edit().putFloat(KEY_GEOFENCE_RADIUS, value).apply()
    }

    fun getGeofenceCenterLat(): Double =
        Double.fromBits(sharedPreferences.getLong(KEY_GEOFENCE_CENTER_LAT, DEFAULT_CENTER_LAT.toBits()))

    fun setGeofenceCenterLat(value: Double) {
        sharedPreferences.edit().putLong(KEY_GEOFENCE_CENTER_LAT, value.toBits()).apply()
    }

    fun getGeofenceCenterLon(): Double =
        Double.fromBits(sharedPreferences.getLong(KEY_GEOFENCE_CENTER_LON, DEFAULT_CENTER_LON.toBits()))

    fun setGeofenceCenterLon(value: Double) {
        sharedPreferences.edit().putLong(KEY_GEOFENCE_CENTER_LON, value.toBits()).apply()
    }

    fun getPollingFrequency(): Long =
        sharedPreferences.getLong(KEY_POLLING_FREQUENCY, DEFAULT_POLLING_FREQUENCY)

    fun setPollingFrequency(value: Long) {
        sharedPreferences.edit().putLong(KEY_POLLING_FREQUENCY, value).apply()
    }

    fun getMeasurementsBeforeTrigger(): Int =
        sharedPreferences.getInt(KEY_MEASUREMENTS, DEFAULT_MEASUREMENTS_BEFORE_TRIGGER)

    fun setMeasurementsBeforeTrigger(value: Int) {
        sharedPreferences.edit().putInt(KEY_MEASUREMENTS, value).apply()
    }

    fun getWebSocketUrl(): String =
        "ws://${getServerIpAddress()}:${getServerPort()}/app"
}
