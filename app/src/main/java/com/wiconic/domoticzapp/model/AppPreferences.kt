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
        const val KEY_MIN_POLLING_DELAY = "minimum_polling_delay"
        const val KEY_MAX_POLLING_DELAY = "maximum_polling_delay"
        const val KEY_ACCURACY_THRESHOLD = "accuracy_threshold"
        const val KEY_GEOFENCE_TRIGGER_OPEN_ENABLED = "geofence_trigger_open_enabled"
        const val KEY_GEOFENCE_TRIGGER_CLOSE_ENABLED = "geofence_trigger_close_enabled"

        const val DEFAULT_SERVER_IP_ADDRESS = "192.168.0.1"
        const val DEFAULT_SERVER_PORT = 8080
        const val DEFAULT_GEOFENCE_RADIUS = 100
        const val DEFAULT_CENTER_LAT = 52.379189
        const val DEFAULT_CENTER_LON = 4.899431
        const val DEFAULT_MIN_POLLING_DELAY = 60L
        const val DEFAULT_MAX_POLLING_DELAY = 1800L
        const val DEFAULT_ACCURACY_THRESHOLD = 3
        const val DEFAULT_GEOFENCE_TRIGGER_OPEN_ENABLED = false
        const val DEFAULT_GEOFENCE_TRIGGER_CLOSE_ENABLED = false
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

    fun getGeofenceRadius(): Int {
        val stringValue = sharedPreferences.getString(KEY_GEOFENCE_RADIUS, DEFAULT_GEOFENCE_RADIUS.toString())
        return stringValue?.toIntOrNull() ?: DEFAULT_GEOFENCE_RADIUS
    }

    fun setGeofenceRadius(value: Int) {
        sharedPreferences.edit().putString(KEY_GEOFENCE_RADIUS, value.toString()).apply()
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

    fun getMinPollingDelay(): Long {
        val stringValue = sharedPreferences.getString(KEY_MIN_POLLING_DELAY, DEFAULT_MIN_POLLING_DELAY.toString())
        return stringValue?.toLongOrNull() ?: DEFAULT_MIN_POLLING_DELAY
    }

    fun setMinPollingDelay(value: Long) {
        sharedPreferences.edit().putString(KEY_MIN_POLLING_DELAY, value.toString()).apply()
    }

    fun getMaxPollingDelay(): Long {
        val stringValue = sharedPreferences.getString(KEY_MAX_POLLING_DELAY, DEFAULT_MAX_POLLING_DELAY.toString())
        return stringValue?.toLongOrNull() ?: DEFAULT_MAX_POLLING_DELAY
    }

    fun setMaxPollingDelay(value: Long) {
        sharedPreferences.edit().putString(KEY_MAX_POLLING_DELAY, value.toString()).apply()
    }

    fun getAccuracyThreshold(): Int {
        val stringValue = sharedPreferences.getString(KEY_ACCURACY_THRESHOLD, DEFAULT_ACCURACY_THRESHOLD.toString())
        return stringValue?.toIntOrNull() ?: DEFAULT_ACCURACY_THRESHOLD
    }

    fun setAccuracyThreshold(value: Int) {
        sharedPreferences.edit().putString(KEY_ACCURACY_THRESHOLD, value.toString()).apply()
    }

    fun getGeofenceTriggerOpenEnabled(): Boolean =
        sharedPreferences.getBoolean(KEY_GEOFENCE_TRIGGER_OPEN_ENABLED, DEFAULT_GEOFENCE_TRIGGER_OPEN_ENABLED)

    fun setGeofenceTriggerOpenEnabled(value: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_GEOFENCE_TRIGGER_OPEN_ENABLED, value).apply()
    }

    fun getGeofenceTriggerCloseEnabled(): Boolean =
        sharedPreferences.getBoolean(KEY_GEOFENCE_TRIGGER_CLOSE_ENABLED, DEFAULT_GEOFENCE_TRIGGER_CLOSE_ENABLED)

    fun setGeofenceTriggerCloseEnabled(value: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_GEOFENCE_TRIGGER_CLOSE_ENABLED, value).apply()
    }

    fun getWebSocketUrl(): String =
        "ws://${getServerIpAddress()}:${getServerPort()}/app"
}
