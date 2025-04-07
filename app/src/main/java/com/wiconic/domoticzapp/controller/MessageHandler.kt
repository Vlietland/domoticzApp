package com.wiconic.domoticzapp.controller

import android.util.Log
import org.json.JSONObject
import org.json.JSONArray

class MessageHandler {
    private var onNewAlertsAvailable: (() -> Unit)? = null
    private var onAlerts: ((JSONArray) -> Unit)? = null
    private var onSetCurrentCamera: ((String) -> Unit)? = null
    private var onImage: ((String) -> Unit)? = null
    private val TAG = "MessageHandler"

    fun setOnNewAlertsAvailable(callback: () -> Unit) {
        this.onNewAlertsAvailable = callback
    }

    fun setOnAlerts(callback: (JSONArray) -> Unit) {
        this.onAlerts = callback
    }

    fun setOnSetCurrentCamera(callback: (String) -> Unit) {
        this.onSetCurrentCamera = callback
    }

    fun setOnImage(callback: (String) -> Unit) {
        this.onImage = callback
    }

    fun onMessageReceived(message: String) {
        try {
            val json = JSONObject(message)
            val messageType = json.optString("type")

            when (messageType) {
                "notification" -> handleNotification(json)
                "alerts" -> handleAlerts(json)
                "cameraImage" -> handleCameraImage(json)
                else -> Log.w(TAG, "Unknown message type: $messageType")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing message: ${e.message}")
        }
    }

    private fun handleNotification(json: JSONObject) {
        val deviceName = json.optString("deviceName", "Unknown notification")
        Log.i(TAG, "Notification received with message: $deviceName")        
        onNewAlertsAvailable?.invoke()
        onSetCurrentCamera?.invoke(deviceName)
        Log.i(TAG, "Device notification received: $deviceName")
    }

   private fun handleAlerts(json: JSONObject) {
        Log.i(TAG, "New alert list received")    
        val alertList = json.optJSONArray("alertList")        
        if (alertList != null) {
            onAlerts?.invoke(alertList)
            Log.i(TAG, "Alerts received: $alertList")  
        }      
    }

    private fun handleCameraImage(json: JSONObject) {
        val cameraId = json.optString("cameraId")
        val imageData = json.optString("imageData", "Unknown data")

        if (imageData.isNotEmpty()) {
            onImage?.invoke(imageData)
            Log.i(TAG, "Image from camera $cameraId received successfully.")
        } else {
            Log.w(TAG, "Empty image data received for camera $cameraId")
        }
    }
}
