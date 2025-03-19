package com.wiconic.domoticzapp.ui

import android.os.Bundle
import android.text.InputType
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import com.wiconic.domoticzapp.R
import com.wiconic.domoticzapp.settings.GeofencePreferenceManager

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings_container, SettingsFragment())
                .commit()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)

            // Set up coordinate validation
            setupCoordinatePreference(
                GeofencePreferenceManager.GEOFENCE_LAT_KEY,
                -90.0..90.0,
                "Latitude must be between -90 and 90 degrees"
            )
            setupCoordinatePreference(
                GeofencePreferenceManager.GEOFENCE_LON_KEY,
                -180.0..180.0,
                "Longitude must be between -180 and 180 degrees"
            )

            // Set up numeric validation for other fields
            setupNumericPreference(
                GeofencePreferenceManager.GEOFENCE_RADIUS_KEY,
                1.0..10000.0,
                "Radius must be between 1 and 10000 meters"
            )
            setupNumericPreference(
                GeofencePreferenceManager.POLLING_FREQUENCY_KEY,
                1000.0..3600000.0,
                "Frequency must be between 1 second (1000) and 1 hour (3600000)"
            )
            setupNumericPreference(
                GeofencePreferenceManager.MEASUREMENTS_BEFORE_TRIGGER_KEY,
                1.0..10.0,
                "Measurements must be between 1 and 10"
            )
        }

        private fun setupCoordinatePreference(key: String, range: ClosedFloatingPointRange<Double>, errorMessage: String) {
            findPreference<EditTextPreference>(key)?.apply {
                setOnBindEditTextListener { editText ->
                    editText.inputType = InputType.TYPE_CLASS_NUMBER or 
                                       InputType.TYPE_NUMBER_FLAG_DECIMAL or 
                                       InputType.TYPE_NUMBER_FLAG_SIGNED
                }
                setOnPreferenceChangeListener { _, newValue ->
                    val value = (newValue as String).toDoubleOrNull()
                    if (value != null && value in range) {
                        true
                    } else {
                        context?.let {
                            android.widget.Toast.makeText(it, errorMessage, android.widget.Toast.LENGTH_SHORT).show()
                        }
                        false
                    }
                }
            }
        }

        private fun setupNumericPreference(key: String, range: ClosedFloatingPointRange<Double>, errorMessage: String) {
            findPreference<EditTextPreference>(key)?.apply {
                setOnBindEditTextListener { editText ->
                    editText.inputType = InputType.TYPE_CLASS_NUMBER
                }
                setOnPreferenceChangeListener { _, newValue ->
                    val value = (newValue as String).toDoubleOrNull()
                    if (value != null && value in range) {
                        true
                    } else {
                        context?.let {
                            android.widget.Toast.makeText(it, errorMessage, android.widget.Toast.LENGTH_SHORT).show()
                        }
                        false
                    }
                }
            }
        }
    }
}
