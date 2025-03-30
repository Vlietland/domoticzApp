package com.wiconic.domoticzapp.ui

import android.os.Bundle
import android.text.InputType
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import com.wiconic.domoticzapp.R
import com.wiconic.domoticzapp.settings.GeofencePreferenceManager
import com.wiconic.domoticzapp.settings.DomoticzPreferenceManager

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportFragmentManager.beginTransaction()
            .replace(R.id.settings_container, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        private lateinit var geofencePreferenceManager: GeofencePreferenceManager
        private lateinit var domoticzPreferenceManager: DomoticzPreferenceManager

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)

            geofencePreferenceManager = GeofencePreferenceManager(requireContext())
            domoticzPreferenceManager = DomoticzPreferenceManager(requireContext())

            setupPreference("server_ip", domoticzPreferenceManager.getServerIp())
            setupPreference("server_port", domoticzPreferenceManager.getServerPort().toString())
            setupPreference(GeofencePreferenceManager.GEOFENCE_LAT_KEY, geofencePreferenceManager.getGeofenceLat().toString())
            setupPreference(GeofencePreferenceManager.GEOFENCE_LON_KEY, geofencePreferenceManager.getGeofenceLon().toString())
            setupPreference(GeofencePreferenceManager.GEOFENCE_RADIUS_KEY, geofencePreferenceManager.getGeofenceRadius().toString())
            setupPreference(GeofencePreferenceManager.POLLING_FREQUENCY_KEY, geofencePreferenceManager.getPollingFrequency().toString())
            setupPreference(GeofencePreferenceManager.MEASUREMENTS_BEFORE_TRIGGER_KEY, geofencePreferenceManager.getMeasurementsBeforeTrigger().toString())
        }

        private fun setupPreference(key: String, defaultValue: String) {
            findPreference<EditTextPreference>(key)?.apply {
                if (text.isNullOrEmpty()) text = defaultValue
                summary = text
                setOnPreferenceChangeListener { _, newValue ->
                    summary = newValue.toString()
                    true
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            finish()
            true
        } else super.onOptionsItemSelected(item)
    }
}
