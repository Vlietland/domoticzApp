package com.wiconic.domoticzapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.wiconic.domoticzapp.R
import com.wiconic.domoticzapp.api.DomoticzAppServerConnection
import com.wiconic.domoticzapp.api.MessageParser
import com.wiconic.domoticzapp.settings.AppPreferenceManager
import com.wiconic.domoticzapp.ui.controller.CameraController
import com.wiconic.domoticzapp.ui.controller.GateController
import com.wiconic.domoticzapp.ui.controller.SwipeGestureController
import com.wiconic.domoticzapp.ui.controller.TextController
import com.wiconic.domoticzapp.ui.controller.GeofenceController
import com.wiconic.domoticzapp.ui.observer.PreferenceObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var cameraController: CameraController
    private lateinit var swipeGestureController: SwipeGestureController
    private lateinit var textController: TextController
    private lateinit var messagesTextView: TextView
    private lateinit var cameraImageView: ImageView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var geofenceController: GeofenceController
    private lateinit var gateController: GateController
    private lateinit var openGateButton: Button
    private lateinit var messageParser: MessageParser
    private lateinit var domoticzAppServerConnection: DomoticzAppServerConnection
    private lateinit var preferenceObserver: PreferenceObserver
    private lateinit var appPreferenceManager: AppPreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById<Toolbar>(R.id.toolbar))

        appPreferenceManager = AppPreferenceManager(this)

        cameraImageView = findViewById<ImageView>(R.id.cameraImageView)
        swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)
        cameraController = CameraController(this, cameraImageView, swipeRefreshLayout, domoticzAppServerConnection)

        messagesTextView = findViewById<TextView>(R.id.messages)
        textController = TextController(messagesTextView)

        messageParser = MessageParser(cameraController, textController)
        domoticzAppServerConnection = DomoticzAppServerConnection(messageParser)

        openGateButton = findViewById<Button>(R.id.button_open_gate)
        gateController = GateController(domoticzAppServerConnection, openGateButton)
        swipeGestureController = SwipeGestureController(this, cameraController)

        lifecycleScope.launch(Dispatchers.IO) { domoticzAppServerConnection.connect(appPreferenceManager.getWebSocketUrl()) }

        geofenceController = GeofenceController(this, appPreferenceManager, gateController)
        geofenceController.initializeGeofence()

        if (appPreferenceManager.getGeofenceEnabled()) geofenceController.startMonitoring()

        preferenceObserver = PreferenceObserver(this, appPreferenceManager, geofenceController, domoticzAppServerConnection)
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(preferenceObserver)

        swipeRefreshLayout.setOnRefreshListener { cameraController.loadCameraImage() }

        findViewById<Button>(R.id.button_open_gate).setOnClickListener { gateController.openGate() }
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
        geofenceController.stopMonitoring()
        domoticzAppServerConnection.disconnect()
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(preferenceObserver)
    }
}
