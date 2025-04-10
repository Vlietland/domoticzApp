package com.wiconic.domoticzapp.controller

import android.util.Log
import android.widget.TextView
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.wiconic.domoticzapp.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlertController(private val sendMessage: (String) -> Unit) {   

    private val deviceToMessageMap = mapOf(
        BuildConfig.DEVICE_1 to BuildConfig.DEVICE_1_MESSAGE,
        BuildConfig.DEVICE_2 to BuildConfig.DEVICE_2_MESSAGE,
        BuildConfig.DEVICE_3 to BuildConfig.DEVICE_3_MESSAGE,
        BuildConfig.DEVICE_4 to BuildConfig.DEVICE_4_MESSAGE,
        BuildConfig.DEVICE_5 to BuildConfig.DEVICE_5_MESSAGE
    )
    private val alertCache = mutableListOf<String>()
    private var alertTextView: TextView? = null
    private var TAG = "AlertController"    

    fun setAlertView(newAlertTextView: TextView) {
        alertTextView = newAlertTextView
    }

    fun onAlerts(alerts: JSONArray) {
        alertCache.clear()            
        for (i in 0 until alerts.length()) {
            val alert = alerts.getJSONObject(i)
            val deviceName = alert.getString("deviceName")
            val timestamp = alert.getDouble("timestamp")
            val alertText = deviceToMessageMap[deviceName]
            if (alertText != null) {
                val formattedTimestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(timestamp.toLong() * 1000))
                val formattedMessage = "[$formattedTimestamp] $alertText"
                alertCache.add(formattedMessage)
                Log.i(TAG, "Messages to the AlertText cache")
            } else {
                Log.w(TAG, "Unknown device name: $deviceName")
            }
        }
        updateAlertView()              
    }

    fun getAlerts() {
        val message = "{\"type\": \"getAlerts\"}"
        Log.d(TAG, "Requesting alert list with message: $message")
        sendMessage(message)
    }

    fun purgeAlerts() {
        alertCache.clear()
        val message = "{\"type\": \"purgeAlerts\"}"
        Log.d(TAG, "Requesting alert list with message: $message")
        sendMessage(message)   
        alertCache.clear() 
        updateAlertView()
    }

    private fun updateAlertView() {
        CoroutineScope(Dispatchers.Main).launch {
            alertTextView?.text = alertCache.joinToString("\n")
        }
    }
}
