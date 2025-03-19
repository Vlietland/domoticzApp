package com.wiconic.domoticzapp.api

import android.content.Context
import android.util.Log
import com.wiconic.domoticzapp.ui.MainActivity
import org.json.JSONObject

class AlarmPushHandler {
    private var mainActivity: MainActivity? = null
    private var webSocketService: DomoticzWebSocketService? = null

    fun initialize(context: Context, serverUrl: String) {
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
                "alarm" -> {
                    val alarmText = json.optString("alarm", "Unknown alarm")
                    val imageUrl = if (json.has("imageUrl")) json.optString("imageUrl") else null
                    mainActivity?.handleAlarmNotification(alarmText, imageUrl)
                    Log.i(TAG, "Received alarm: $alarmText")
                }
                else -> {
                    Log.w(TAG, "Unknown message type received")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing message: ${e.message}")
        }
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
