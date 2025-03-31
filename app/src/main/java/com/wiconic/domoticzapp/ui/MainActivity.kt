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
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.wiconic.domoticzapp.R
import com.wiconic.domoticzapp.api.DomoticzAppServerConnection
import com.wiconic.domoticzapp.api.MessageParser
import com.wiconic.domoticzapp.settings.AppPreferenceManager
import com.wiconic.domoticzapp.ui.controller.CameraController
import com.wiconic.domoticzapp.ui.controller.GateController
import com.wiconic.domoticzapp.ui.controller.CameraSwipeController
import com.wiconic.domoticzapp.ui.controller.NotificationSwipeController
import com.wiconic.domoticzapp.ui.controller.TextController
import com.wiconic.domoticzapp.ui.controller.GeofenceController
import com.wiconic.domoticzapp.ui.observer.PreferenceObserver
import com.wiconic.domoticzapp.ui.SettingsActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var cameraController: CameraController
    private lateinit var cameraSwipeController: CameraSwipeController
    private lateinit var textController: TextController
    private lateinit var messagesTextView: TextView
    private lateinit var cameraImageView: ImageView
    private lateinit var notificationCardView: CardView
    private lateinit var geofenceController: GeofenceController
    private lateinit var notificationSwipeController: NotificationSwipeController
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

        cameraImageView = findViewById(R.id.cameraImageView)
        messagesTextView = findViewById(R.id.messages)
        openGateButton = findViewById(R.id.button_open_gate)
        notificationCardView = findViewById(R.id.notification_card)

        messageParser = MessageParser()
        domoticzAppServerConnection = DomoticzAppServerConnection(messageParser)

        cameraController = CameraController(this, cameraImageView, domoticzAppServerConnection)
        cameraSwipeController = CameraSwipeController(this, cameraController, cameraImageView)
        cameraImageView.setOnTouchListener(cameraSwipeController)
        textController = TextController(messagesTextView)
        notificationSwipeController = NotificationSwipeController(this, notificationCardView, messagesTextView)
        notificationCardView.setOnTouchListener(notificationSwipeController)

        gateController = GateController(domoticzAppServerConnection, openGateButton)
        geofenceController = GeofenceController(this, appPreferenceManager, gateController)

        messageParser.setupControllers(cameraController, textController)

        lifecycleScope.launch(Dispatchers.IO) { 
            domoticzAppServerConnection.connect(appPreferenceManager.getWebSocketUrl())
        }

        geofenceController.initializeGeofence()
        if (appPreferenceManager.getGeofenceEnabled()) geofenceController.startMonitoring()
        
        preferenceObserver = PreferenceObserver(this, appPreferenceManager, geofenceController, domoticzAppServerConnection)
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(preferenceObserver)

        openGateButton.setOnClickListener { gateController.openGate() }

        cameraController.loadNewImageFromCurrentCamera()
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
