package com.wiconic.domoticzapp.api

import android.util.Log
import com.wiconic.domoticzapp.ui.controller.CameraController
import com.wiconic.domoticzapp.ui.controller.TextController
import org.json.JSONObject

class MessageParser {

    private var cameraController: CameraController? = null
    private var textController: TextController? = null

    fun setupControllers(cameraController: CameraController?, textController: TextController?) {
        this.cameraController = cameraController
        this.textController = textController
    }

    fun parseMessage(message: String) {
        if (cameraController == null || textController == null) {
            Log.e(TAG, "Controllers are not initialized yet. Call setupControllers() first.")
            return
        }

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
        if (textController != null) {
            textController!!.addMessage(messageText)
            Log.i(TAG, "Text notification received: $messageText")
        }
        if (imageData.isNotEmpty() && cameraController != null) {
            cameraController!!.handleIncomingImage(imageData)
            Log.i(TAG, "Image notification received with message: $messageText")
        }
    }

    companion object {
        private const val TAG = "MessageParser"
    }
}
