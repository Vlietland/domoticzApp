package com.wiconic.domoticzapp.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.appcompat.widget.Toolbar
import com.wiconic.domoticzapp.DomoticzApp
import com.wiconic.domoticzapp.R
import com.wiconic.domoticzapp.api.AlarmPushHandler
import com.wiconic.domoticzapp.api.GateOperationCallback
import com.wiconic.domoticzapp.settings.DomoticzPreferenceManager
import com.wiconic.domoticzapp.settings.GeofencePreferenceManager
import com.wiconic.domoticzapp.ui.controller.CameraController
import com.wiconic.domoticzapp.ui.controller.GateController
import com.wiconic.domoticzapp.ui.controller.GeofenceController
import com.wiconic.domoticzapp.ui.controller.SwipeGestureHandler
import com.wiconic.domoticzapp.ui.observer.PreferenceObserver

class MainActivity : AppCompatActivity(), GateOperationCallback {

    private lateinit var cameraImageView: ImageView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var preferenceManager: DomoticzPreferenceManager
    private lateinit var geofencePreferenceManager: GeofencePreferenceManager
    private lateinit var alarmPushHandler: AlarmPushHandler

    private lateinit var cameraController: CameraController
    private lateinit var geofenceController: GeofenceController
    private lateinit var gateController: GateController
    private lateinit var swipeGestureHandler: SwipeGestureHandler
    private lateinit var preferenceObserver: PreferenceObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_main)
            val toolbar = findViewById<Toolbar>(R.id.toolbar)
            setSupportActionBar(toolbar)

            cameraImageView = findViewById(R.id.cameraImageView)
            swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)

            val app = application as DomoticzApp
            preferenceManager = DomoticzPreferenceManager(this)
            geofencePreferenceManager = GeofencePreferenceManager(this)
            alarmPushHandler = AlarmPushHandler().apply {
                setMainActivity(this@MainActivity)
                initialize(this@MainActivity, preferenceManager.getWebSocketUrl())
            }

            cameraController = CameraController(this, cameraImageView, swipeRefreshLayout, alarmPushHandler)
            geofenceController = GeofenceController(this, geofencePreferenceManager)
            gateController = GateController(this, alarmPushHandler.webSocketService!!)
            swipeGestureHandler = SwipeGestureHandler(cameraController)
            // Initialize geofence controller first
            if (geofencePreferenceManager.isGeofenceEnabled()) {
                geofenceController.initializeGeofence()
            }

            preferenceObserver = PreferenceObserver(this, preferenceManager, geofencePreferenceManager, geofenceController, alarmPushHandler)

            PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(preferenceObserver)

            swipeRefreshLayout.setOnRefreshListener { cameraController.loadCameraImage() }

            updateGeofenceStatusUI()

            initializeBasicFeatures()

            cameraImageView.setOnTouchListener { _, event ->
                swipeGestureHandler.handleSwipe(cameraImageView, event)
            }

        } catch (e: Exception) {
            handleInitializationError(e)
        }
    }

    fun updateAlarmUI(alarmText: String, imageData: String?) {
        try {
            val alarmTextView = findViewById<TextView>(R.id.alarm_text)
            alarmTextView.text = alarmText
            cameraController.updateAlarmUI(imageData)
        } catch (e: Exception) {
            showToast("Error updating alarm UI: ${e.message}")
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

    private fun initializeBasicFeatures() {
        try {
            val buttonOpenGate = findViewById<Button>(R.id.button_open_gate)
            buttonOpenGate.setOnClickListener {
                gateController.openGate(this)
            }
        } catch (e: Exception) {
            handleInitializationError(e)
        }
    }

    fun handleAlarmNotification(alarmText: String, imageData: String?) {
        runOnUiThread {
            updateAlarmUI(alarmText, imageData)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        geofenceController.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
        super.onDestroy()
        geofenceController.onDestroy()
        alarmPushHandler.cleanup()
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(preferenceObserver)
    }

    override fun onGateOperationSuccess() {
        runOnUiThread {
            showToast(getString(R.string.gate_opened))
        }
    }

    override fun onGateOperationFailure(errorMessage: String) {
        runOnUiThread {
            showToast("${getString(R.string.gate_error)}: $errorMessage")
        }
    }

    private fun handleInitializationError(e: Exception) {
        e.printStackTrace()
        showToast("Error initializing app: ${e.message}")
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
