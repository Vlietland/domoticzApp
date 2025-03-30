package com.wiconic.domoticzapp.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import com.wiconic.domoticzapp.R
import com.wiconic.domoticzapp.settings.AppPreferenceManager

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

        private lateinit var appPreferenceManager: AppPreferenceManager

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)
            appPreferenceManager = AppPreferenceManager(requireContext())

            setupPreference("server_ip", appPreferenceManager.getServerIpAddress())
            setupPreference("server_port", appPreferenceManager.getServerPort().toString())
            setupPreference("geofence_lat", appPreferenceManager.getGeofenceCenterLat().toString())
            setupPreference("geofence_lon", appPreferenceManager.getGeofenceCenterLon().toString())
            setupPreference("geofence_radius", appPreferenceManager.getGeofenceRadius().toString())
            setupPreference("polling_frequency", appPreferenceManager.getPollingFrequency().toString())
            setupPreference("measurements_before_trigger", appPreferenceManager.getMeasurementsBeforeTrigger().toString())
        }

        private fun setupPreference(key: String, defaultValue: String) {
            findPreference<EditTextPreference>(key)?.apply {
                if (text.isNullOrEmpty()) text = defaultValue
                summary = text
                setOnPreferenceChangeListener { _, newValue ->
                    text = newValue.toString()
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
