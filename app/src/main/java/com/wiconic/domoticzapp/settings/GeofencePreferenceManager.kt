package com.wiconic.domoticzapp.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class GeofencePreferenceManager(context: Context) {
    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun getLatitude(): Double {
        return prefs.getString(GEOFENCE_LAT_KEY, DEFAULT_LAT)?.toDoubleOrNull() ?: DEFAULT_LAT.toDouble()
    }

    fun getLongitude(): Double {
        return prefs.getString(GEOFENCE_LON_KEY, DEFAULT_LON)?.toDoubleOrNull() ?: DEFAULT_LON.toDouble()
    }

    fun getRadius(): Float {
        return prefs.getString(GEOFENCE_RADIUS_KEY, DEFAULT_RADIUS)?.toFloatOrNull() ?: DEFAULT_RADIUS.toFloat()
    }

    fun getPollingFrequency(): Long {
        return prefs.getString(POLLING_FREQUENCY_KEY, DEFAULT_POLLING_FREQUENCY)?.toLongOrNull() 
            ?: DEFAULT_POLLING_FREQUENCY.toLong()
    }

    fun getMeasurementsBeforeTrigger(): Int {
        return prefs.getString(MEASUREMENTS_BEFORE_TRIGGER_KEY, DEFAULT_MEASUREMENTS)?.toIntOrNull() 
            ?: DEFAULT_MEASUREMENTS.toInt()
    }

    fun isGeofenceEnabled(): Boolean {
        return prefs.getBoolean(GEOFENCE_ENABLED_KEY, false)
    }

    fun setGeofenceEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(GEOFENCE_ENABLED_KEY, enabled).apply()
    }

    companion object {
        const val GEOFENCE_ENABLED_KEY = "geofence_enabled"
        const val GEOFENCE_LAT_KEY = "geofence_lat"
        const val GEOFENCE_LON_KEY = "geofence_lon"
        const val GEOFENCE_RADIUS_KEY = "geofence_radius"
        const val POLLING_FREQUENCY_KEY = "polling_frequency"
        const val MEASUREMENTS_BEFORE_TRIGGER_KEY = "measurements_before_trigger"

        private const val DEFAULT_LAT = "52.3676"
        private const val DEFAULT_LON = "4.9041"
        private const val DEFAULT_RADIUS = "100"
        private const val DEFAULT_POLLING_FREQUENCY = "60000"
        private const val DEFAULT_MEASUREMENTS = "3"
    }
}
