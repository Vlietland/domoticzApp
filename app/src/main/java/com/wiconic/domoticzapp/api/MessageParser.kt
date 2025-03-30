package com.wiconic.domoticzapp.api

import android.util.Log
import com.wiconic.domoticzapp.ui.controller.CameraController
import com.wiconic.domoticzapp.ui.controller.TextController
import org.json.JSONObject

class MessageParser(
    private val cameraController: CameraController,
    private val textController: TextController,
) {

    fun parseMessage(message: String) {
        try {
            val json = JSONObject(message)
            val messageType = json.optString("type")

            if (messageType == "notification") {
                handleNotification(json)
            } else {
                Log.w(TAG, "Unknown message type: $messageType")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing message: ${e.message}")
        }
    }

    private fun handleNotification(json: JSONObject) {
        val messageText = json.optString("message", "Unknown notification")
        val imageData = json.optString("imageData", "")

        if (imageData.isNotEmpty()) {
            cameraController.handleIncomingImage(imageData)
            Log.i(TAG, "Image notification received with message: $messageText")
        } else {
            textController.addMessage(messageText)
            Log.i(TAG, "Text notification received: $messageText")
        }
    }

    companion object {
        private const val TAG = "MessageParser"
    }
}
