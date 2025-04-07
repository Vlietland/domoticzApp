package com.wiconic.domoticzapp.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.wiconic.domoticzapp.R
import com.wiconic.domoticzapp.service.MessageHandler
import com.wiconic.domoticzapp.controller.CameraSwipeController
import com.wiconic.domoticzapp.controller.AlertSwipeController
import com.wiconic.domoticzapp.service.DomoticzAppService
import com.wiconic.domoticzapp.ui.SettingsActivity

class MainActivity : AppCompatActivity() {
    private lateinit var mainModelView: MainModelView
    private lateinit var cameraSwipeController: CameraSwipeController
    private lateinit var geofenceIcon: ImageView
    private lateinit var serverConnectionIcon: ImageView    
    private lateinit var alertSwipeController: AlertSwipeController
    private lateinit var alertTextView: TextView
    private lateinit var cameraImageView: ImageView
    private lateinit var alertCardView: CardView
    private lateinit var openGateButton: Button
    private lateinit var domoticzAppService: DomoticzAppService
    private lateinit var messageHandler: MessageHandler
    private var isBound = false    

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById<Toolbar>(R.id.toolbar))

        mainModelView = ViewModelProvider(this)[MainModelView::class.java]
        openGateButton = findViewById(R.id.button_open_gate)
        cameraImageView = findViewById(R.id.cameraImageView)
        alertTextView = findViewById(R.id.alertTextView)
        alertCardView = findViewById(R.id.alertCardView)        
        geofenceIcon = findViewById(R.id.geofenceIcon)
        serverConnectionIcon = findViewById(R.id.serverConnectionIcon)

        val serviceIntent = Intent(this, DomoticzAppService::class.java)
        startService(serviceIntent)
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            Log.d("MainActivity", "Service connected successfully")            
            val binder = service as DomoticzAppService.LocalBinder
            domoticzAppService = binder.getService()
            isBound = true

            messageHandler = domoticzAppService.getMessageHandler()
            mainModelView.initializeComponents(this@MainActivity, cameraImageView, openGateButton, geofenceIcon, serverConnectionIcon, messageHandler)
            mainModelView.getAlertController().setAlertView(alertTextView)
            mainModelView.getCameraController().setImageView(cameraImageView)
            cameraSwipeController = CameraSwipeController(this@MainActivity, mainModelView.getCameraController(), cameraImageView)
            cameraImageView.setOnTouchListener(cameraSwipeController)
            alertSwipeController = AlertSwipeController(this@MainActivity, alertCardView, mainModelView.getAlertController()::purgeAlerts)
            alertCardView.setOnTouchListener(alertSwipeController)
            openGateButton.setOnClickListener { mainModelView.getGateController().openGate() }
            PreferenceManager.getDefaultSharedPreferences(this@MainActivity).registerOnSharedPreferenceChangeListener(mainModelView.getPreferenceObserver())
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isBound = false
            Log.d("MainActivity", "Service disconnected")
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
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(mainModelView.getPreferenceObserver())

        if (isBound) {
            unbindService(connection)
            isBound = false
        }
    }
}
