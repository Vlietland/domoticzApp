package com.wiconic.domoticzapp.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import android.util.Log

class GeofencePreferenceManager(context: Context) {
    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val domoticzPreferenceManager = DomoticzPreferenceManager(context)

    fun getLatitude(): Double {
        val defaultLat = domoticzPreferenceManager.getEnvValue("GEOFENCE_DEFAULT_LAT")!!
        return prefs.getString(GEOFENCE_LAT_KEY, defaultLat)?.toDoubleOrNull() ?: defaultLat.toDouble()
    }

    fun getLongitude(): Double {
        val defaultLon = domoticzPreferenceManager.getEnvValue("GEOFENCE_DEFAULT_LON")!!
        return prefs.getString(GEOFENCE_LON_KEY, defaultLon)?.toDoubleOrNull() ?: defaultLon.toDouble()
    }

    fun getRadius(): Float {
        val defaultRadius = domoticzPreferenceManager.getEnvValue("GEOFENCE_DEFAULT_RADIUS")!!
        return prefs.getString(GEOFENCE_RADIUS_KEY, defaultRadius)?.toFloatOrNull() ?: defaultRadius.toFloat()
    }

    fun getPollingFrequency(): Long {
        val defaultFrequency = domoticzPreferenceManager.getEnvValue("GEOFENCE_DEFAULT_POLLING_FREQUENCY")!!
        return prefs.getString(POLLING_FREQUENCY_KEY, defaultFrequency)?.toLongOrNull() 
            ?: defaultFrequency.toLong()
    }

    fun getMeasurementsBeforeTrigger(): Int {
        val defaultMeasurements = domoticzPreferenceManager.getEnvValue("GEOFENCE_DEFAULT_MEASUREMENTS_BEFORE_TRIGGER")!!
        return prefs.getString(MEASUREMENTS_BEFORE_TRIGGER_KEY, defaultMeasurements)?.toIntOrNull() 
            ?: defaultMeasurements.toInt()
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
    }
}
