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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.wiconic.domoticzapp.R
import com.wiconic.domoticzapp.connectivity.AppServerConnector
import com.wiconic.domoticzapp.controller.MessageHandler
import com.wiconic.domoticzapp.controller.CameraController
import com.wiconic.domoticzapp.controller.GateController
import com.wiconic.domoticzapp.controller.CameraSwipeController
import com.wiconic.domoticzapp.controller.AlertSwipeController
import com.wiconic.domoticzapp.controller.AlertController
import com.wiconic.domoticzapp.controller.GeofenceController
import com.wiconic.domoticzapp.model.AppPreferences
import com.wiconic.domoticzapp.ui.SettingsActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
        mainModelView.initializeComponents(this, cameraImageView, openGateButton, geofenceIcon, serverConnectionIcon)

        mainModelView.getAlertController().setAlertView(alertTextView)
        mainModelView.getCameraController().setImageView(cameraImageView)

        cameraSwipeController = CameraSwipeController(this, mainModelView.getCameraController(), cameraImageView)
        cameraImageView.setOnTouchListener(cameraSwipeController)

        alertSwipeController = AlertSwipeController(this, alertCardView, mainModelView.getAlertController()::purgeAlerts)
        alertCardView.setOnTouchListener(alertSwipeController)

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(mainModelView.getPreferenceObserver())

        openGateButton.setOnClickListener { mainModelView.getGateController().openGate() }
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
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(mainModelView.getPreferenceObserver())
    }
}
