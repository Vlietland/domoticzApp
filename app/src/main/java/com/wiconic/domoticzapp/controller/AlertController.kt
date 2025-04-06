package com.wiconic.domoticzapp.controller

import android.util.Log
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale  
import com.wiconic.domoticzapp.BuildConfig

class AlertController(private val sendMessage: (String) -> Unit) {   

    private var onNewAlerts: (() -> Unit)? = null
    private val deviceToMessageMap = mapOf(
        BuildConfig.DEVICE_1 to BuildConfig.DEVICE_1_MESSAGE,
        BuildConfig.DEVICE_2 to BuildConfig.DEVICE_2_MESSAGE,
        BuildConfig.DEVICE_3 to BuildConfig.DEVICE_3_MESSAGE,
        BuildConfig.DEVICE_4 to BuildConfig.DEVICE_4_MESSAGE,
        BuildConfig.DEVICE_5 to BuildConfig.DEVICE_5_MESSAGE
    )
    private var TAG = "AlertController"    

    private val alertCache = mutableListOf<String>()  // Temporary cache for alert messages

    fun setOnNewAlertsCallback(callback: () -> Unit) {
        onNewAlerts = callback
    }

    fun onAlerts(alerts: String) {
        val alertList = alerts.split(",").map { it.trim() }
        for (deviceName in alertList) {
            val alertText = deviceToMessageMap[deviceName]
            if (alertText != null) {
                val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                val formattedMessage = "[$timestamp] $alertText"
                alertCache.add(formattedMessage)
                onNewAlerts?.invoke()
                Log.i(TAG, "Message added for device: $deviceName - $formattedMessage")
            } else {
                Log.w(TAG, "Unknown device name: $deviceName")
            }
        }
    }

    fun loadAlerts() {
        val message = "{\"type\": \"getAlerts\"}"
        Log.d(TAG, "Requesting alert list with message: $message")
        sendMessage(message)
    }

    fun purgeAlerts() {
        alertCache.clear()
        val message = "{\"type\": \"purgeAlerts\"}"
        Log.d(TAG, "Requesting alert list with message: $message")
        sendMessage(message)
        onNewAlerts?.invoke()       
    }

    fun getCachedAlerts(): List<String> {
        return alertCache.toList()
    }
}
