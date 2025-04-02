package com.wiconic.domoticzapp.ui

import android.content.Context
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wiconic.domoticzapp.api.DomoticzAppServerConnection
import com.wiconic.domoticzapp.api.MessageParser
import com.wiconic.domoticzapp.model.GeofenceModel
import com.wiconic.domoticzapp.settings.AppPreferenceManager
import com.wiconic.domoticzapp.ui.controller.CameraController
import com.wiconic.domoticzapp.ui.controller.GateController
import com.wiconic.domoticzapp.ui.controller.GeofenceController
import com.wiconic.domoticzapp.ui.controller.TextController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainModelView : ViewModel() {
    private lateinit var domoticzAppServerConnection: DomoticzAppServerConnection
    private lateinit var messageParser: MessageParser
    private lateinit var appPreferenceManager: AppPreferenceManager
    private lateinit var gateController: GateController
    private lateinit var geofenceController: GeofenceController
    private lateinit var cameraController: CameraController
    private lateinit var textController: TextController

    fun initializeComponents(context: Context, cameraImageView: ImageView, messagesTextView: TextView, openGateButton: Button) {
        if (!::appPreferenceManager.isInitialized) {
            appPreferenceManager = AppPreferenceManager(context)
            messageParser = MessageParser()
            domoticzAppServerConnection = DomoticzAppServerConnection(messageParser)
            cameraController = CameraController(context, cameraImageView, domoticzAppServerConnection)
            textController = TextController(messagesTextView)
            gateController = GateController(domoticzAppServerConnection, openGateButton)
            geofenceController = GeofenceController(context, appPreferenceManager, gateController)
            messageParser.setupControllers(cameraController, textController)

            viewModelScope.launch(Dispatchers.IO) {
                domoticzAppServerConnection.connect(appPreferenceManager.getWebSocketUrl())
            }

            geofenceController.initializeGeofence()
            if (appPreferenceManager.getGeofenceEnabled()) geofenceController.startMonitoring()
        }
    }

    fun getCameraController() = cameraController
    fun getTextController() = textController
    fun getGeofenceController() = geofenceController
    fun getGateController() = gateController
    fun getDomoticzAppServerConnection() = domoticzAppServerConnection
    fun getAppPreferenceManager() = appPreferenceManager

    override fun onCleared() {
        super.onCleared()
        if (::geofenceController.isInitialized) {
            geofenceController.stopMonitoring()
        }
        if (::domoticzAppServerConnection.isInitialized) {
            domoticzAppServerConnection.disconnect()
        }
    }
}
