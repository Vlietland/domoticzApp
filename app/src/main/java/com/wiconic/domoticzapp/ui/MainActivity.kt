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
    private lateinit var mainModelView: MainModelView
    private lateinit var cameraSwipeController: CameraSwipeController
    private lateinit var notificationSwipeController: NotificationSwipeController
    private lateinit var messagesTextView: TextView
    private lateinit var cameraImageView: ImageView
    private lateinit var notificationCardView: CardView
    private lateinit var openGateButton: Button
    private lateinit var preferenceObserver: PreferenceObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById<Toolbar>(R.id.toolbar))

        mainModelView = ViewModelProvider(this)[MainModelView::class.java]
        openGateButton = findViewById(R.id.button_open_gate)
        cameraImageView = findViewById(R.id.cameraImageView)
        messagesTextView = findViewById(R.id.messages)
        mainModelView.initializeComponents(this, cameraImageView, messagesTextView, openGateButton)

        notificationCardView = findViewById(R.id.notification_card)
        mainModelView.getTextController().updateTextView(messagesTextView)
        messagesTextView.text = mainModelView.getTextController().getMessages()

        mainModelView.getCameraController().setImageView(cameraImageView)

        cameraSwipeController = CameraSwipeController(this, mainModelView.getCameraController(), cameraImageView)
        cameraImageView.setOnTouchListener(cameraSwipeController)

        notificationSwipeController = NotificationSwipeController(this, notificationCardView, mainModelView.getTextController())
        notificationCardView.setOnTouchListener(notificationSwipeController)

        preferenceObserver = PreferenceObserver(
            this,
            mainModelView.getAppPreferenceManager(),
            mainModelView.getGeofenceController(),
            mainModelView.getDomoticzAppServerConnection()
        )

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(preferenceObserver)

        openGateButton.setOnClickListener { mainModelView.getGateController().openGate() }

        mainModelView.getCameraController().loadNewImageFromCurrentCamera()
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
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(preferenceObserver)
    }
}
