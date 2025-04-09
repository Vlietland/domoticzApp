package com.wiconic.domoticzapp.ui

import android.os.Bundle
import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import android.widget.Button
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wiconic.domoticzapp.connectivity.AppServerConnector
import com.wiconic.domoticzapp.controller.AlertController
import com.wiconic.domoticzapp.controller.CameraController
import com.wiconic.domoticzapp.controller.GateController
import com.wiconic.domoticzapp.controller.GeofenceController
import com.wiconic.domoticzapp.controller.NotificationController
import com.wiconic.domoticzapp.controller.PreferenceObserver
import com.wiconic.domoticzapp.controller.ServerIconController
import com.wiconic.domoticzapp.model.AppPreferences
import com.wiconic.domoticzapp.model.Geofence
import com.wiconic.domoticzapp.service.MessageHandler
import com.wiconic.domoticzapp.service.DomoticzAppService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainModelView : ViewModel() {
    private lateinit var appPreferences: AppPreferences
    private lateinit var gateController: GateController
    private lateinit var geofenceController: GeofenceController
    private lateinit var cameraController: CameraController
    private lateinit var alertController: AlertController
    private lateinit var serverIconController: ServerIconController
    private lateinit var preferenceObserver: PreferenceObserver
    private lateinit var geofence: Geofence
    private lateinit var notificationController: NotificationController
    private lateinit var domoticzAppService: DomoticzAppService
    private lateinit var appServerConnector: AppServerConnector
    private lateinit var appContext: Context
    private var initialized = false    

    fun initializeControllers(context: Context, service: DomoticzAppService) {
        appContext = context
        domoticzAppService = service
        appServerConnector = domoticzAppService.getAppServerConnector() 
        appPreferences = domoticzAppService.getAppPreferences()           
        geofence = Geofence(appPreferences)
        alertController = AlertController(appServerConnector::sendMessage)
        appServerConnector.addOnWebSocketActiveListener(alertController::onWebSocketActiveListeners)
        notificationController = NotificationController(alertController::getAlerts)

        cameraController = CameraController(appServerConnector::sendMessage)
        appServerConnector.addOnWebSocketActiveListener(cameraController::onWebSocketActiveListeners) 

        gateController = GateController(appServerConnector::sendMessage)
        geofenceController = GeofenceController(gateController::openGate, appPreferences)
        geofence.setOnGeofenceStateChangeCallback(geofenceController::onIsWithinGeofenceCallback)  

        serverIconController = ServerIconController()   
        appServerConnector.addOnWebSocketActiveListener(serverIconController::onWebSocketActiveListeners)        

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
    }

    fun setupUIComponents(
        cameraImageView: ImageView,
        geofenceIcon: ImageView,
        serverConnectionIcon: ImageView,
        alertTextView: TextView,
        gateButton: Button
    ) {
        cameraController.setImageView(cameraImageView)
        alertController.setAlertView(alertTextView)
        geofenceController.setGeofenceIcon(geofenceIcon)
        gateController.setGateButton(gateButton)        
    }

    fun getCameraController() = cameraController
    fun getAlertController() = alertController
    fun getGeofenceController() = geofenceController
    fun getGateController() = gateController
    fun getPreferenceObserver() = preferenceObserver
    fun getAppPreferences() = appPreferences
    fun getDomoticzServiceManager() = domoticzAppService
    fun isInitialized(): Boolean = initialized

    fun refreshView()
    {   
        alertController.getAlerts()
        cameraController.loadNewImageFromCurrentCamera()
        serverIconController.updateServerConnectionIcon()
    }


    override fun onCleared() {
        super.onCleared()
        if (::geofenceController.isInitialized) {
            geofenceController.stopGeofenceMonitoring()
        }
    }
}
