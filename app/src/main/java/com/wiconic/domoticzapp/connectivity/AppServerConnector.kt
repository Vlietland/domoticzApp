package com.wiconic.domoticzapp.connectivity

import android.os.Handler
import android.os.Looper
import android.util.Log
import okhttp3.*
import java.util.concurrent.TimeUnit
import com.wiconic.domoticzapp.model.AppPreferences

class AppServerConnector(private val appPreferences: AppPreferences) {
    private val client = OkHttpClient.Builder().pingInterval(30, TimeUnit.SECONDS).build()
    private var webSocket: WebSocket? = null
    private var isConnected = false
    private val onWebSocketActiveListeners = mutableListOf<(Boolean) -> Unit>()
    private var onMessageReceived: ((String) -> Unit)? = null
    private var reconnectAttempts = 0
    private var handler = Handler(Looper.getMainLooper())
    private val RECONNECT_DELAY_BASE = 2000L
    private val MAX_RECONNECT_DELAY = 30000L
    private var TAG = "AppServerConnector"

    fun addOnWebSocketActiveListener(listener: (Boolean) -> Unit) {
        onWebSocketActiveListeners.add(listener)
    }

    fun removeOnWebSocketActiveListener(listener: (Boolean) -> Unit) {
        onWebSocketActiveListeners.remove(listener)
    }

    fun setOnMessageReceivedCallback(callback: (String) -> Unit) {
        this.onMessageReceived = callback
    }

    fun initializeConnection() {
        val serverUrl = appPreferences.getWebSocketUrl()
        if (isConnected) disconnect()
        connect(serverUrl)
    }

    private fun createWebSocketListener() = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            isConnected = true
            reconnectAttempts = 0
            Log.d(TAG, "WebSocket connected successfully")
            onWebSocketActiveListeners.forEach { it.invoke(true) }
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.d(TAG, "Message received: $text")
            onMessageReceived?.invoke(text)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            isConnected = false
            Log.e(TAG, "WebSocket connection failed: ${t.message}")
            onWebSocketActiveListeners.forEach { it.invoke(false) }            
            scheduleReconnection()
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            isConnected = false
            Log.d(TAG, "WebSocket closed: $reason")
            scheduleReconnection()
        }
    }

    private fun scheduleReconnection() {
        reconnectAttempts++
        val delay = (RECONNECT_DELAY_BASE * Math.pow(2.0, reconnectAttempts.toDouble())).toLong().coerceAtMost(MAX_RECONNECT_DELAY)

        Log.d(TAG, "Scheduling reconnection attempt #$reconnectAttempts in ${delay / 1000} seconds")

        handler.postDelayed({
            Log.d(TAG, "Attempting to reconnect...")
            initializeConnection()
        }, delay)
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
            scheduleReconnection()
        }
        return success
    }

    fun isConnected() = isConnected

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
            reconnectAttempts = 0
        }
    }
}
