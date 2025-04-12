package com.wiconic.domoticzapp.controller

import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.ViewModel
import com.wiconic.domoticzapp.BuildConfig

class NotificationController(
    private val getAlerts: () -> Unit,
    private val playNotification: (String) -> Unit

) : ViewModel() {

    private val TAG = "NotificationController"

    fun onNewAlertsAvailable() { 
        playNotification.invoke("")
        getAlerts()
        Log.i(TAG, "Notification received, requesting Alert list.")        
    }
}
