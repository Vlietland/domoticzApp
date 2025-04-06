package com.wiconic.domoticzapp.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import com.wiconic.domoticzapp.R
import com.wiconic.domoticzapp.model.AppPreferences

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

        private lateinit var appPreferences: AppPreferences

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)
            appPreferences = AppPreferences(requireContext())

            setupPreference("server_ip", appPreferences.getServerIpAddress())
            setupPreference("server_port", appPreferences.getServerPort().toString())
            setupPreference("geofence_lat", appPreferences.getGeofenceCenterLat().toString())
            setupPreference("geofence_lon", appPreferences.getGeofenceCenterLon().toString())
            setupPreference("geofence_radius", appPreferences.getGeofenceRadius().toString())
            setupPreference("polling_frequency", appPreferences.getPollingFrequency().toString())
            setupPreference("measurements_before_trigger", appPreferences.getMeasurementsBeforeTrigger().toString())
        }

        private fun setupPreference(key: String, defaultValue: String) {
            val preference = findPreference<androidx.preference.Preference>(key)

            if (preference is EditTextPreference) {
                if (preference.text.isNullOrEmpty()) preference.text = defaultValue
                preference.summary = preference.text
                preference.setOnPreferenceChangeListener { _, newValue ->
                    preference.text = newValue.toString()
                    preference.summary = newValue.toString()
                    true
                }
            } else if (preference is androidx.preference.SeekBarPreference) {
                preference.setOnPreferenceChangeListener { _, newValue ->
                    preference.summary = newValue.toString()
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
