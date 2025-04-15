package com.wiconic.domoticzapp.controller

import android.util.Log
import androidx.lifecycle.ViewModel

class NotificationController(
    private val getAlerts: () -> Unit,
    private val playNotification: (String) -> Unit

) : ViewModel() {

    private val TAG = "NotificationController"

    fun onNewAlertsAvailable() { 
        playNotification.invoke("")
        getAlerts()
        Log.d(TAG, "Notification received, requesting Alert list.")        
    }
}
