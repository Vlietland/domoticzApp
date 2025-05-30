package com.wiconic.domoticzapp.service

import android.util.Log
import org.json.JSONObject
import org.json.JSONArray
import android.content.Context

class MessageHandler(private val context: Context) {
    private val TAG = "MessageHandler"
    private var onNewAlertsAvailable: (() -> Unit)? = null
    private var onAlerts: ((JSONArray) -> Unit)? = null
    private var onSetCurrentCamera: ((String) -> Unit)? = null
    private var onImage: ((String) -> Unit)? = null
    private var onWeather: ((String) -> Unit)? = null    

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

    fun setOnWeather(callback: (String) -> Unit) {
        this.onWeather = callback
    }

    fun onMessageReceived(message: String) {
        try {
            val json = JSONObject(message)
            val messageType = json.optString("type")

            when (messageType) {
                "notification" -> handleNotification(json)
                "alerts" -> handleAlerts(json)
                "cameraImage" -> handleCameraImage(json)
                "weather" -> handleWeatherData(json)
                else -> Log.w(TAG, "Unknown message type: $messageType")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing message: ${e.message}")
        }
    }

    private fun handleNotification(json: JSONObject) {
        val deviceName = json.optString("deviceName", "Unknown notification")
        Log.d(TAG, "Notification received with message: $deviceName")     
        onNewAlertsAvailable?.invoke()
        onSetCurrentCamera?.invoke(deviceName)
    }

    private fun handleAlerts(json: JSONObject) {
        Log.d(TAG, "New alert list received")    
        val alertList = json.optJSONArray("alertList")        
        if (alertList != null) {
            onAlerts?.invoke(alertList)
        }      
    }

    private fun handleCameraImage(json: JSONObject) {
        val cameraId = json.optString("cameraId")
        val imageData = json.optString("imageData", "Unknown data")
        if (imageData.isNotEmpty()) {
            onImage?.invoke(imageData)
            Log.d(TAG, "Image from camera $cameraId received successfully.")
        } else {
            Log.w(TAG, "Empty image data received for camera $cameraId")
        }
    }

    private fun handleWeatherData(json: JSONObject) {
        val temp = json.optString("outsideTemp", "Unknown data")
        if (temp.isNotEmpty()) {
            onWeather?.invoke(temp)
            Log.d(TAG, "Weather data received: $temp")
        } else {
            Log.w(TAG, "Empty weather data received")
        }
    }
}
