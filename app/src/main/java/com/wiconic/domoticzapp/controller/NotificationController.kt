package com.wiconic.domoticzapp.controller

import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.ViewModel
import com.wiconic.domoticzapp.BuildConfig

class NotificationController(
    private val sendMessage: (String) -> Unit
    //private var notificationIcon: ImageView
) : ViewModel() {

    private val TAG = "NotificationController"

    fun onNewAlertsAvailable() {
        val message = "{\"type\": \"getAlerts\"}"
        Log.d(TAG, "Requesting alert list with message: $message")
        sendMessage(message)
     //   activateNotificationIcon()
    }
/*
    fun updateNotificationIcon(iconView: ImageView) {
        notificationIcon = iconView
    }

    fun activateNotificationIcon() {
        notificationIcon.visibility = View.VISIBLE
        notificationIcon.alpha = 1.0f
        Log.d(TAG, "Notification icon activated to indicate new alerts.")
    }

    fun deactivateNotificationIcon() {
        notificationIcon.alpha = 0.5f
        Log.d(TAG, "Notification icon deactivated.")
    } */
}
