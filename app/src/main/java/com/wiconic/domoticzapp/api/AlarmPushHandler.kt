package com.wiconic.domoticzapp.api

import android.content.Context
import android.util.Log
import com.wiconic.domoticzapp.ui.MainActivity
import org.json.JSONObject
import android.util.Base64

class AlarmPushHandler {

    private var mainActivity: MainActivity? = null
    var webSocketService: DomoticzWebSocketService? = null
    private var serverUrl: String = ""
    private var context: Context? = null
    private val TAG = "AlarmPushHandler"

    fun initialize(context: Context, serverUrl: String) {
        this.context = context
        this.serverUrl = serverUrl
        webSocketService = DomoticzWebSocketService(
            context = context,
            serverUrl = serverUrl,
            alarmHandler = this
        ).also { it.connect() }
    }

    fun setMainActivity(activity: MainActivity) {
        mainActivity = activity
    }

    fun handleIncomingPush(jsonPayload: String) {
        try {
            val json = JSONObject(jsonPayload)
            when (json.optString("type")) {
                "notification" -> {
                    val messageText = json.optString("message", "Unknown notification")
                    val imageData = json.optString("imageData", null)
                    mainActivity?.handleAlarmNotification(messageText, imageData)
                    Log.i(TAG, "Received notification: $messageText")
                }
                "cameraImage" -> {
                    val cameraId = json.optString("cameraId", "default")
                    val imageData = json.optString("imageData", null)
                    mainActivity?.handleAlarmNotification("Camera Image from $cameraId", imageData)
                    Log.i(TAG, "Received camera image from $cameraId")
                }
                else -> {
                    Log.w(TAG, "Unknown message type received")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing message: ${e.message}")
        }
    }

    fun sendMessage(message: String): Boolean {
        return webSocketService?.sendMessage(message) ?: false
    }

    fun cleanup() {
        webSocketService?.disconnect()
        webSocketService = null
        mainActivity = null
    }

    companion object {
        private const val TAG = "AlarmPushHandler"
    }
}
