package com.wiconic.domoticzapp.ui

import android.util.Log
import android.content.Context
import android.os.Build
import android.app.NotificationManager
import android.content.Intent
import android.provider.Settings
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import android.widget.TextView
import android.widget.Button
import androidx.lifecycle.ViewModel
import com.wiconic.domoticzapp.connectivity.AppServerConnector
import com.wiconic.domoticzapp.connectivity.SoundConnector
import com.wiconic.domoticzapp.controller.AlertController
import com.wiconic.domoticzapp.controller.CameraController
import com.wiconic.domoticzapp.controller.GateController
import com.wiconic.domoticzapp.controller.GeofenceController
import com.wiconic.domoticzapp.controller.NotificationController
import com.wiconic.domoticzapp.controller.PreferenceObserver
import com.wiconic.domoticzapp.controller.ServerIconController
import com.wiconic.domoticzapp.controller.WeatherController
import com.wiconic.domoticzapp.model.AppPreferences
import com.wiconic.domoticzapp.model.Geofence
import com.wiconic.domoticzapp.service.MessageHandler
import com.wiconic.domoticzapp.service.DomoticzAppService

class MainModelView : ViewModel() {
    private val TAG = "MainModelView"      
    private lateinit var appPreferences: AppPreferences
    private lateinit var gateController: GateController
    private lateinit var geofenceController: GeofenceController
    private lateinit var cameraController: CameraController
    private lateinit var alertController: AlertController
    private lateinit var serverIconController: ServerIconController
    private lateinit var weatherController: WeatherController    
    private lateinit var preferenceObserver: PreferenceObserver
    private lateinit var geofence: Geofence
    private lateinit var notificationController: NotificationController
    private lateinit var domoticzAppService: DomoticzAppService
    private lateinit var appServerConnector: AppServerConnector
    private lateinit var soundConnector: SoundConnector
    private lateinit var appContext: Context
    private lateinit var temperatureTextView: TextView
    private var initialized = false

    fun checkAndRequestDndAccess(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val hasAccess = notificationManager.isNotificationPolicyAccessGranted

            Log.d("DND", "Notification policy access = $hasAccess")

            if (!hasAccess) {
                AlertDialog.Builder(context)
                    .setTitle("Permission Needed")
                    .setMessage(
                        "To play alerts while Do Not Disturb is on, please allow DomoticzApp to manage DND settings."
                    )
                    .setPositiveButton("Grant Access") { _, _ ->
                        val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                        context.startActivity(intent)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        }
    }

    fun initializeControllers(context: Context, service: DomoticzAppService) {
        appContext = context
        soundConnector = SoundConnector(context)
        domoticzAppService = service
        appServerConnector = domoticzAppService.getAppServerConnector() 
        appPreferences = domoticzAppService.getAppPreferences()           
        geofence = Geofence(appPreferences)

        alertController = AlertController(appServerConnector::sendMessage)
        appServerConnector.addOnWebSocketActiveListener(alertController::onWebSocketActiveListeners)         

        notificationController = NotificationController(alertController::getAlerts, soundConnector::playNotification)
        
        cameraController = CameraController(appServerConnector::sendMessage)
        appServerConnector.addOnWebSocketActiveListener(cameraController::onWebSocketActiveListeners)         

        gateController = GateController(appServerConnector::sendMessage)
        geofenceController = GeofenceController(gateController::openGate, appPreferences)
        geofence.setOnGeofenceStateChangeCallback(geofenceController::onIsWithinGeofenceCallback)  

        serverIconController = ServerIconController(appServerConnector)   
        appServerConnector.addOnWebSocketActiveListener(serverIconController::onWebSocketActiveListeners)

        weatherController = WeatherController(appServerConnector::sendMessage)
        appServerConnector.addOnWebSocketActiveListener(weatherController::onWebSocketActiveListeners)        

        preferenceObserver = PreferenceObserver(
            appContext,
            appPreferences = appPreferences,
            initializeConnection = appServerConnector::initializeConnection,
            initializeGeofence = geofenceController::startGeofenceMonitoring,
            stopGeofence = geofenceController::stopGeofenceMonitoring
        ) 
        initialized = true        
    }

    fun setupMessageHandlerCallbacks(messageHandler: MessageHandler) {
        messageHandler.setOnNewAlertsAvailable(notificationController::onNewAlertsAvailable)
        messageHandler.setOnAlerts(alertController::onAlerts)
        messageHandler.setOnSetCurrentCamera(cameraController::setCurrentCamera)
        messageHandler.setOnImage(cameraController::onImage)
        messageHandler.setOnWeather(weatherController::onWeatherDataReceived)        
    }

    fun setupUIComponents(
        cameraImageView: ImageView,
        geofenceIcon: ImageView,
        serverConnectionIcon: ImageView,
        temperatureTextView: TextView,
        alertTextView: TextView,
        openGateButton: Button,
        closeGateButton: Button,
        cameraProgressBar: ProgressBar
    ) {
        cameraController.setImageViewAndProgress(cameraImageView, cameraProgressBar) 
        alertController.setAlertView(alertTextView)
        serverIconController.setServerConnectionIcon(serverConnectionIcon)
        geofenceController.setGeofenceIcon(geofenceIcon)
        gateController.setGateButtons(openGateButton, closeGateButton)
        weatherController.setTemperatureView(temperatureTextView)
    }

    fun getCameraController() = cameraController
    fun getAlertController() = alertController
    fun getGeofenceController() = geofenceController
    fun getGateController() = gateController
    fun getPreferenceObserver() = preferenceObserver
    fun getAppPreferences() = appPreferences
    fun getDomoticzServiceManager() = domoticzAppService
    fun isInitialized(): Boolean = initialized

    override fun onCleared() {
        super.onCleared()
        if (::geofenceController.isInitialized) {
            geofenceController.stopGeofenceMonitoring()
        }
    }
}
