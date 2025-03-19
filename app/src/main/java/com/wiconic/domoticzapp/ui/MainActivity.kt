package com.wiconic.domoticzapp.ui

import android.Manifest
import android.view.View
import android.widget.ImageView
import com.wiconic.domoticzapp.api.AlarmPushHandler
import com.wiconic.domoticzapp.api.GateOperationCallback
import com.wiconic.domoticzapp.api.OpenGateHandler
import android.widget.TextView
import com.squareup.picasso.Picasso
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.wiconic.domoticzapp.DomoticzApp
import com.wiconic.domoticzapp.geofence.DynamicGeofenceManager
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.preference.PreferenceManager
import com.wiconic.domoticzapp.R
import com.wiconic.domoticzapp.settings.DomoticzPreferenceManager
import com.wiconic.domoticzapp.settings.GeofencePreferenceManager

class MainActivity : AppCompatActivity(), GateOperationCallback {
    private val preferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        when (key) {
            DomoticzPreferenceManager.SERVER_IP_KEY, DomoticzPreferenceManager.SERVER_PORT_KEY -> {
                // Reconnect WebSocket with new server settings
                alarmPushHandler?.apply {
                    cleanup()
                    initialize(
                        context = this@MainActivity,
                        serverUrl = preferenceManager.getWebSocketUrl()
                    )
                }
            }
            GeofencePreferenceManager.GEOFENCE_ENABLED_KEY -> {
                if (geofencePreferenceManager.isGeofenceEnabled()) {
                    initializeGeofence()
                } else {
                    geofenceManager?.stopMonitoring()
                }
                updateGeofenceStatusUI()
            }
            GeofencePreferenceManager.GEOFENCE_LAT_KEY,
            GeofencePreferenceManager.GEOFENCE_LON_KEY,
            GeofencePreferenceManager.GEOFENCE_RADIUS_KEY,
            GeofencePreferenceManager.POLLING_FREQUENCY_KEY,
            GeofencePreferenceManager.MEASUREMENTS_BEFORE_TRIGGER_KEY -> {
                if (geofencePreferenceManager.isGeofenceEnabled()) {
                    updateGeofenceFromPreferences()
                }
            }
        }
    }
    private lateinit var preferenceManager: DomoticzPreferenceManager
    private lateinit var geofencePreferenceManager: GeofencePreferenceManager
    private var geofenceManager: DynamicGeofenceManager? = null
    private var alarmPushHandler: AlarmPushHandler? = null
    
    // Default values for geofence settings
    private val defaultLat = 52.3676
    private val defaultLon = 4.9041
    private val defaultRadius = 100.0f
    private val defaultPollingFrequency = 60000L // 1 minute in milliseconds
    private val defaultMeasurementsBeforeTrigger = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(preferenceChangeListener)
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_main)
            
            // Set up toolbar
            val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
            setSupportActionBar(toolbar)
            
            val app = application as DomoticzApp
            // Initialize preferences
            preferenceManager = DomoticzPreferenceManager(this)
            geofencePreferenceManager = GeofencePreferenceManager(this)

            // Set up geofence status UI
            updateGeofenceStatusUI()

            if (app.isGooglePlayServicesAvailable && geofencePreferenceManager.isGeofenceEnabled()) {
                initializeGeofence()
            } else if (!app.isGooglePlayServicesAvailable) {
                Toast.makeText(this, "Google Play Services not available. Geofencing is disabled.", Toast.LENGTH_LONG).show()
            }

            // Initialize WebSocket connection
            alarmPushHandler = AlarmPushHandler().apply {
                setMainActivity(this@MainActivity)
                initialize(
                    context = this@MainActivity,
                    serverUrl = preferenceManager.getWebSocketUrl()
                )
            }

            // Initialize non-Google Play Services dependent features
            initializeBasicFeatures()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error initializing app: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateGeofenceStatusUI() {
        try {
            val statusCard = findViewById<View>(R.id.geofence_status_card)
            val statusIcon = findViewById<ImageView>(R.id.geofence_status_icon)
            val statusText = findViewById<TextView>(R.id.geofence_status_text)

            if (geofencePreferenceManager.isGeofenceEnabled()) {
                statusCard.visibility = View.VISIBLE
                statusIcon.setColorFilter(getColor(R.color.statusActive))
                statusText.setTextColor(getColor(R.color.statusActive))
                statusText.text = getString(R.string.geofence_active)
            } else {
                statusCard.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initializeGeofence() {
        try {
            updateGeofenceFromPreferences()
            checkLocationPermission()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error initializing Google Play Services features: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun initializeBasicFeatures() {
        try {
            // Set up the Open Gate button
            findViewById<Button>(R.id.button_open_gate)?.setOnClickListener {
                try {
                    val openGateHandler = OpenGateHandler()
                    openGateHandler.setCallback(this)
                    openGateHandler.openGate()
                    Toast.makeText(this, getString(R.string.gate_opening), Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, getString(R.string.gate_error), Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error initializing basic features: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun initializeGeofenceManager(
        lat: Double,
        lon: Double,
        radius: Float,
        measurements: Int,
        frequency: Long
    ) {
        try {
            geofenceManager = DynamicGeofenceManager(
                context = this,
                geofenceCenterLat = lat,
                geofenceCenterLon = lon,
                geofenceRadius = radius,
                measurementsBeforeTrigger = measurements,
                pollingFrequency = frequency
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error initializing geofence: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateGeofenceFromPreferences() {
        try {
            // Stop current monitoring
            geofenceManager?.stopMonitoring()

            // Initialize geofence manager with values from preferences
            initializeGeofenceManager(
                geofencePreferenceManager.getLatitude(),
                geofencePreferenceManager.getLongitude(),
                geofencePreferenceManager.getRadius(),
                geofencePreferenceManager.getMeasurementsBeforeTrigger(),
                geofencePreferenceManager.getPollingFrequency()
            )

            // Restart monitoring if we have permission
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                geofenceManager?.startMonitoring()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error updating geofence: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkLocationPermission() {
        try {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            } else {
                geofenceManager?.startMonitoring()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, getString(R.string.error_location_permission), Toast.LENGTH_LONG).show()
        }
    }

    fun updateAlarmUI(alarmText: String, imageUrl: String?) {
        try {
            val alarmTextView = findViewById<TextView>(R.id.alarm_text)
            val alarmImageView = findViewById<ImageView>(R.id.alarm_image)
            
            alarmTextView?.text = alarmText
            
            // Handle image loading and visibility
            alarmImageView?.let { imageView ->
                if (!imageUrl.isNullOrEmpty()) {
                    imageView.visibility = View.VISIBLE
                    Picasso.get()
                        .load(imageUrl)
                        .fit()
                        .centerCrop()
                        .into(imageView, object : com.squareup.picasso.Callback {
                            override fun onSuccess() {
                                // Ensure the image takes up 1/3 of the screen height
                                val params = imageView.layoutParams
                                params.height = (resources.displayMetrics.heightPixels * 0.33).toInt()
                                imageView.layoutParams = params
                            }
                            override fun onError(e: Exception?) {
                                imageView.visibility = View.GONE
                                Log.e("MainActivity", "Error loading image: ${e?.message}")
                            }
                        })
                } else {
                    imageView.visibility = View.GONE
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error updating alarm UI: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun handleAlarmNotification(alarmText: String, imageUrl: String?) {
        runOnUiThread {
            updateAlarmUI(alarmText, imageUrl)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        try {
            if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && 
                grantResults.isNotEmpty() && 
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                geofenceManager?.startMonitoring()
                Toast.makeText(this, "Location permission granted, geofence monitoring started", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Location permission denied, geofence monitoring disabled", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error handling permission result: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        try {
            super.onDestroy()
            geofenceManager?.stopMonitoring()
            alarmPushHandler?.cleanup()
            PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // GateOperationCallback implementation
    override fun onGateOperationSuccess() {
        runOnUiThread {
            Toast.makeText(this, getString(R.string.gate_opened), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onGateOperationFailure(errorMessage: String) {
        runOnUiThread {
            Toast.makeText(this, "${getString(R.string.gate_error)}: $errorMessage", Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}
