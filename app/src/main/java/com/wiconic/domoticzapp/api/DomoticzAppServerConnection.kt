package com.wiconic.domoticzapp.api

import android.util.Log
import okhttp3.*
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DomoticzAppServerConnection(private val messageParser: MessageParser) {

    private val client = OkHttpClient.Builder()
        .pingInterval(30, TimeUnit.SECONDS)
        .build()

    private var webSocket: WebSocket? = null
    private var serverUrl: String = ""

    private var isConnected = false

    fun connect(serverUrl: String) {
        this.serverUrl = serverUrl
        val request = Request.Builder().url(serverUrl).build()
        webSocket = client.newWebSocket(request, createWebSocketListener())
        Log.d(TAG, "Connecting to WebSocket URL: $serverUrl")
    }

    private fun createWebSocketListener() = object : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response) {
            isConnected = true
            Log.d(TAG, "WebSocket connected successfully")
            messageParser.onWebsocketOpen()
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            //Log.d(TAG, "Message received: $text")
            CoroutineScope(Dispatchers.Main).launch {
                messageParser.parseMessage(text)
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            isConnected = false
            Log.e(TAG, "WebSocket connection failed: ${t.message}")
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            isConnected = false
            Log.d(TAG, "WebSocket closed: $reason")
        }
    }

    fun sendMessage(message: String): Boolean {
        if (!isConnected || webSocket == null) {
            Log.e(TAG, "Unable to send message, WebSocket is not connected")
            return false
        }

        if (webSocket!!.queueSize() > 0) {
            Log.w(TAG, "WebSocket message queue is full. Messages might be delayed.")
        }
        val success = webSocket!!.send(message)

        if (!success) {
            Log.e(TAG, "Failed to send message via WebSocket. Attempting to reconnect.")
            reconnect()  // Optionally implement a reconnect mechanism
        }
        return success
    }

    private fun reconnect() {
        disconnect()
        connect(serverUrl)
    }

    fun disconnect() {
        webSocket?.close(1000, "App closed")
        webSocket = null
        isConnected = false
    }

    companion object {
        private const val TAG = "DomoticzAppServerConnection"
    }
}
