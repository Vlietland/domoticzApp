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

    fun onWebsocketOpen() {
        cameraController?.loadNewImageFromCurrentCamera()            
    }

    fun parseMessage(message: String) {
        if (cameraController == null || textController == null) {
            Log.e(TAG, "Controllers are not initialized yet. Call setupControllers() first.")
            return
        }

        try {
            val json = JSONObject(message)
            val messageType = json.optString("type")

            when (messageType) {
                "notification" -> handleNotification(json)
                "cameraImage" -> handleCameraImage(json)
                else -> Log.w(TAG, "Unknown message type: $messageType")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing message: ${e.message}")
        }
    }

    private fun handleNotification(json: JSONObject) {
        val messageText = json.optString("message", "Unknown notification")
        val imageData = json.optString("imageData", "")
        Log.d(TAG, "Received image data. Length: ${imageData.length}")        

        textController?.addMessage(messageText)
        Log.i(TAG, "Text notification received: $messageText")

        if (imageData.isNotEmpty()) {
            cameraController?.handleIncomingImage(imageData)
            Log.i(TAG, "Image notification received with message: $messageText")
        }
    }

    private fun handleCameraImage(json: JSONObject) {
        val cameraId = json.optString("cameraId")
        val imageData = json.optString("imageData", "")

        if (imageData.isNotEmpty()) {
            cameraController?.handleIncomingImage(imageData)
            Log.i(TAG, "Image from camera $cameraId received successfully.")
        } else {
            Log.w(TAG, "Empty image data received for camera $cameraId")
        }
    }

    companion object {
        private const val TAG = "MessageParser"
    }
}
