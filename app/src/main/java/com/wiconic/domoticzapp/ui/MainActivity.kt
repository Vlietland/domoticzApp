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
import com.wiconic.domoticzapp.geofence.DynamicGeofenceManager
import com.wiconic.domoticzapp.R

class MainActivity : AppCompatActivity(), GateOperationCallback {
    private lateinit var geofenceManager: DynamicGeofenceManager
    private lateinit var alarmPushHandler: AlarmPushHandler
    
    // Default values for geofence settings
    private val defaultLat = 52.3676
    private val defaultLon = 4.9041
    private val defaultRadius = 100.0f
    private val defaultPollingFrequency = 60000L // 1 minute in milliseconds
    private val defaultMeasurementsBeforeTrigger = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the AlarmPushHandler with a reference to this activity
        alarmPushHandler = AlarmPushHandler()
        alarmPushHandler.setMainActivity(this)
        
        // Initialize UI with default values
        findViewById<EditText>(R.id.input_geofence_lat).setText(defaultLat.toString())
        findViewById<EditText>(R.id.input_geofence_lon).setText(defaultLon.toString())
        findViewById<EditText>(R.id.input_geofence_radius).setText(defaultRadius.toString())
        findViewById<EditText>(R.id.input_polling_frequency).setText(defaultPollingFrequency.toString())
        findViewById<EditText>(R.id.input_measurements_before_trigger).setText(defaultMeasurementsBeforeTrigger.toString())

        // Initialize geofence manager with default values
        initializeGeofenceManager(
            defaultLat,
            defaultLon,
            defaultRadius,
            defaultMeasurementsBeforeTrigger,
            defaultPollingFrequency
        )

        // Set up the Open Gate button
        findViewById<Button>(R.id.button_open_gate).setOnClickListener {
            val openGateHandler = OpenGateHandler()
            openGateHandler.setCallback(this)
            openGateHandler.openGate()
            Toast.makeText(this, "Gate opening command sent", Toast.LENGTH_SHORT).show()
        }

        // Set up the Save Settings button
        findViewById<Button>(R.id.button_save_settings).setOnClickListener {
            saveGeofenceSettings()
        }

        // Check for location permission
        checkLocationPermission()
    }

    private fun initializeGeofenceManager(
        lat: Double,
        lon: Double,
        radius: Float,
        measurements: Int,
        frequency: Long
    ) {
        geofenceManager = DynamicGeofenceManager(
            context = this,
            geofenceCenterLat = lat,
            geofenceCenterLon = lon,
            geofenceRadius = radius,
            measurementsBeforeTrigger = measurements,
            pollingFrequency = frequency
        )
    }

    private fun saveGeofenceSettings() {
        try {
            val geofenceLatInput = findViewById<EditText>(R.id.input_geofence_lat).text.toString()
            val geofenceLonInput = findViewById<EditText>(R.id.input_geofence_lon).text.toString()
            val geofenceRadiusInput = findViewById<EditText>(R.id.input_geofence_radius).text.toString()
            val pollingFrequencyInput = findViewById<EditText>(R.id.input_polling_frequency).text.toString()
            val measurementsBeforeTriggerInput = findViewById<EditText>(R.id.input_measurements_before_trigger).text.toString()

            // Validate inputs
            if (geofenceLatInput.isEmpty() || geofenceLonInput.isEmpty() || 
                geofenceRadiusInput.isEmpty() || pollingFrequencyInput.isEmpty() || 
                measurementsBeforeTriggerInput.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return
            }

            // Stop current monitoring
            geofenceManager.stopMonitoring()

            // Update geofence settings
            initializeGeofenceManager(
                geofenceLatInput.toDouble(),
                geofenceLonInput.toDouble(),
                geofenceRadiusInput.toFloat(),
                measurementsBeforeTriggerInput.toInt(),
                pollingFrequencyInput.toLong()
            )

            // Restart monitoring if we have permission
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                geofenceManager.startMonitoring()
                Toast.makeText(this, "Settings saved and geofence updated", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error saving settings: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkLocationPermission() {
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
            geofenceManager.startMonitoring()
        }
    }

    fun updateAlarmUI(alarmText: String, imageUrl: String?) {
        val alarmTextView = findViewById<TextView>(R.id.alarm_text)
        val alarmImageView = findViewById<ImageView>(R.id.alarm_image)
        
        alarmTextView.text = alarmText
        if (imageUrl != null) {
            alarmImageView.visibility = View.VISIBLE
            Picasso.get().load(imageUrl).into(alarmImageView)
        } else {
            alarmImageView.visibility = View.GONE
        }
    }

    // Method to handle incoming alarm notifications
    fun handleAlarmNotification(alarmText: String, imageUrl: String?) {
        // Update UI on the main thread
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
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && 
            grantResults.isNotEmpty() && 
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            geofenceManager.startMonitoring()
            Toast.makeText(this, "Location permission granted, geofence monitoring started", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Location permission denied, geofence monitoring disabled", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        geofenceManager.stopMonitoring()
    }

    // GateOperationCallback implementation
    override fun onGateOperationSuccess() {
        runOnUiThread {
            Toast.makeText(this, "Gate opened successfully", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onGateOperationFailure(errorMessage: String) {
        runOnUiThread {
            Toast.makeText(this, "Gate operation failed: $errorMessage", Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}
