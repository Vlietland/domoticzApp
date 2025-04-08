package com.wiconic.domoticzapp.ui

import android.content.Context
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wiconic.domoticzapp.connectivity.AppServerConnector
import com.wiconic.domoticzapp.service.MessageHandler
import com.wiconic.domoticzapp.controller.CameraController
import com.wiconic.domoticzapp.controller.GateController
import com.wiconic.domoticzapp.controller.GeofenceController
import com.wiconic.domoticzapp.controller.AlertController
import com.wiconic.domoticzapp.controller.NotificationController
import com.wiconic.domoticzapp.controller.ServerIconController
import com.wiconic.domoticzapp.controller.PreferenceObserver
import com.wiconic.domoticzapp.ui.MainActivity
import com.wiconic.domoticzapp.model.Geofence
import com.wiconic.domoticzapp.model.AppPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainModelView : ViewModel() {
    private lateinit var appServerConnector: AppServerConnector
    private lateinit var appPreferences: AppPreferences
    private lateinit var gateController: GateController
    private lateinit var geofenceController: GeofenceController
    private lateinit var cameraController: CameraController
    private lateinit var alertController: AlertController
    private lateinit var serverIconController: ServerIconController
    private lateinit var preferenceObserver: PreferenceObserver
    private lateinit var geofence: Geofence
    private lateinit var notificationController: NotificationController

    fun initializeComponents(
        context: Context, 
        cameraImageView: ImageView, 
        openGateButton: Button, 
        geofenceIcon: ImageView, 
        serverConnectionIcon: ImageView,
        messageHandler: MessageHandler
        ) {
        if (!::appPreferences.isInitialized) {
            appPreferences = AppPreferences(context)
            //appServerConnector = AppServerConnector(appPreferences)

            geofence = Geofence(appPreferences)
            alertController = AlertController(appServerConnector::sendMessage)
            cameraController = CameraController(context, cameraImageView, appServerConnector::sendMessage)
            gateController = GateController(appServerConnector::sendMessage, openGateButton)
            geofenceController = GeofenceController(context, gateController::openGate, appPreferences, geofenceIcon)
            serverIconController = ServerIconController(context, serverConnectionIcon)
            notificationController = NotificationController(alertController::getAlerts)

            preferenceObserver = PreferenceObserver(
                context,
                appPreferences = appPreferences,
                initializeConnection = getAppServerConnector()::initializeConnection,
                initializeGeofence = getGeofenceController()::startGeofenceMonitoring,
                stopGeofence = getGeofenceController()::stopGeofenceMonitoring
            )

            //appServerConnector.setOnMessageReceivedCallback(messageHandler::onMessageReceived)
            //appServerConnector.addOnWebSocketActiveListener(cameraController::onWebSocketActiveListeners)
            //appServerConnector.addOnWebSocketActiveListener(alertController::onWebSocketActiveListeners)
            //appServerConnector.addOnWebSocketActiveListener(serverIconController::onWebSocketActiveListeners)
    
            messageHandler.setOnNewAlertsAvailable(notificationController::onNewAlertsAvailable)
            messageHandler.setOnAlerts(alertController::onAlerts)
            messageHandler.setOnSetCurrentCamera(cameraController::setCurrentCamera)
            messageHandler.setOnImage(cameraController::onImage)

            geofence.setOnGeofenceStateChangeCallback(geofenceController::onIsWithinGeofenceCallback)

            viewModelScope.launch(Dispatchers.IO) {
                appServerConnector.initializeConnection()
            }
        }
    }

    fun getCameraController() = cameraController
    fun getAlertController() = alertController
    fun getGeofenceController() = geofenceController
    fun getGateController() = gateController
    fun getAppServerConnector() = appServerConnector
    fun getPreferenceObserver() = preferenceObserver
    fun getAppPreferences() = appPreferences

    override fun onCleared() {
        super.onCleared()
        if (::geofenceController.isInitialized) {
            geofenceController.stopGeofenceMonitoring()
        }
        if (::appServerConnector.isInitialized) {
            appServerConnector.disconnect()
        }
    }
}
