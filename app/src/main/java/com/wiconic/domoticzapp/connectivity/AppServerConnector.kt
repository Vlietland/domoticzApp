package com.wiconic.domoticzapp.connectivity

import android.util.Log
import okhttp3.*
import java.util.concurrent.TimeUnit
import com.wiconic.domoticzapp.model.AppPreferences

class AppServerConnector(private val appPreferences: AppPreferences) {
    private val client = OkHttpClient.Builder().pingInterval(30, TimeUnit.SECONDS).build()
    private var webSocket: WebSocket? = null
    private var isConnected = false
    private var onWebSocketOpen: (() -> Unit)? = null
    private var onMessageReceived: ((String) -> Unit)? = null
    private var onWebSocketClosed: (() -> Unit)? = null
    private var TAG = "DomoticzAppServerConnection"    

    fun setOnWebSocketOpenCallback(callback: () -> Unit) {
        this.onWebSocketOpen = callback
    }

    fun setOnMessageReceivedCallback(callback: (String) -> Unit) {
        this.onMessageReceived = callback
    }

    fun setOnWebSocketClosedCallback(callback: () -> Unit) {
        this.onWebSocketClosed = callback
    }

    fun initializeConnection() {
        val serverUrl = appPreferences.getWebSocketUrl() 
        if (isConnected) disconnect()
        connect(serverUrl)
    }    

    private fun createWebSocketListener() = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            isConnected = true
            Log.d(TAG, "WebSocket connected successfully")
            onWebSocketOpen?.invoke()
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.d(TAG, "Message received: $text")
            onMessageReceived?.invoke(text)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            isConnected = false
            Log.e(TAG, "WebSocket connection failed: ${t.message}")
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            isConnected = false
            Log.d(TAG, "WebSocket closed: $reason")
            onWebSocketClosed?.invoke()
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
            initializeConnection()
        }
        return success
    }

    private fun connect(serverUrl: String) {
        val request = Request.Builder().url(serverUrl).build()
        webSocket = client.newWebSocket(request, createWebSocketListener())
        Log.d(TAG, "Connecting to WebSocket URL: $serverUrl")
    }

    fun disconnect() {
        if (isConnected) {
            webSocket?.close(1000, "App closed")
            webSocket = null
            isConnected = false
        }
    }
}
