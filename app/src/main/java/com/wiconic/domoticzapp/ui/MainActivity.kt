    package com.wiconic.domoticzapp.ui

    import android.content.ComponentName
    import android.content.Context
    import android.content.Intent
    import android.content.ServiceConnection
    import android.content.res.Configuration
    import android.os.Build
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
    import androidx.core.view.WindowCompat
    import androidx.core.view.WindowInsetsCompat
    import androidx.core.view.WindowInsetsControllerCompat
    import androidx.lifecycle.ViewModelProvider
    import androidx.preference.PreferenceManager
    import com.wiconic.domoticzapp.R
    import com.wiconic.domoticzapp.controller.AlertSwipeController
    import com.wiconic.domoticzapp.controller.CameraSwipeController
    import com.wiconic.domoticzapp.service.DomoticzAppService

    class MainActivity : AppCompatActivity() {
        private val TAG = "MainActivity"
        private lateinit var mainModelView: MainModelView
        private lateinit var cameraSwipeController: CameraSwipeController
        private lateinit var alertSwipeController: AlertSwipeController
        private lateinit var geofenceIcon: ImageView
        private lateinit var serverConnectionIcon: ImageView    
        private lateinit var alertTextView: TextView
        private lateinit var cameraImageView: ImageView
        private lateinit var alertCardView: CardView
        private lateinit var openGateButton: Button
        private lateinit var closeGateButton: Button
        private var domoticzAppService: DomoticzAppService? = null
        private var serviceBound = false
        
        private val serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(className: ComponentName, service: IBinder) {
                Log.d(TAG, "Service connected in MainActivity")
                val binder = service as DomoticzAppService.LocalBinder
                domoticzAppService = binder.getService()
                serviceBound = true
                onServiceCreatedCallback()
            }
            override fun onServiceDisconnected(arg0: ComponentName) {
                serviceBound = false
                Log.d(TAG, "Service disconnected in MainActivity")
            }
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            Log.d(TAG, "Oncreate called")
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)
            setSupportActionBar(findViewById<Toolbar>(R.id.toolbar))
            if (savedInstanceState == null) {
                Log.d(TAG, "Starting DomoticzAppService")
                val serviceIntent = Intent(this, DomoticzAppService::class.java)
                startService(serviceIntent)
            }
            val bindIntent = Intent(this, DomoticzAppService::class.java)
            bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        }

        fun onServiceCreatedCallback() {
            Log.d(TAG,"Callback called")
            mainModelView = ViewModelProvider(this)[MainModelView::class.java]
            if (!mainModelView.isInitialized()) {
                mainModelView.checkAndRequestDndAccess(this)
                mainModelView.initializeControllers(this, domoticzAppService!!)
                mainModelView.setupMessageHandlerCallbacks(domoticzAppService!!.getMessageHandler())
            }
            initializeUIComponents()
            setupUIComponentsInViewModel()
            setupControllersAndListeners()
        }

        private fun initializeUIComponents() {
            openGateButton = findViewById(R.id.button_open_gate)
            closeGateButton = findViewById(R.id.button_close_gate)
            cameraImageView = findViewById(R.id.cameraImageView)
            alertTextView = findViewById(R.id.alertTextView)
            alertCardView = findViewById(R.id.alertCardView)        
            geofenceIcon = findViewById(R.id.geofenceIcon)
            serverConnectionIcon = findViewById(R.id.serverConnectionIcon)
        }
        
        private fun setupUIComponentsInViewModel() {
            mainModelView.setupUIComponents(cameraImageView, geofenceIcon, serverConnectionIcon, alertTextView,
                openGateButton, closeGateButton
            )
            PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(mainModelView.getPreferenceObserver())        
        }
        
        private fun setupControllersAndListeners() {
            mainModelView.getAlertController().setAlertView(alertTextView)
            cameraSwipeController = CameraSwipeController(this, mainModelView.getCameraController(), cameraImageView)
            cameraImageView.setOnTouchListener(cameraSwipeController)
            alertSwipeController = AlertSwipeController(this, alertCardView, mainModelView.getAlertController()::purgeAlerts)
            alertCardView.setOnTouchListener(alertSwipeController)
            openGateButton.setOnClickListener { mainModelView.getGateController().openGate() }
            closeGateButton.setOnClickListener { mainModelView.closeGate() }
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
            Log.d("MainActivity", "onDestroy() called")
            if (serviceBound) {
                unbindService(serviceConnection)
                serviceBound = false
            }
            PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(mainModelView.getPreferenceObserver())
        }

        override fun onWindowFocusChanged(hasFocus: Boolean) {
            super.onWindowFocusChanged(hasFocus)
            if (hasFocus) handleSystemUiVisibility()
        }

        private fun handleSystemUiVisibility() {
            val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
            windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
                supportActionBar?.hide()
            } else {
                windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
                supportActionBar?.show()
            }
        }
    }
