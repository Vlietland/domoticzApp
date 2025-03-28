package com.wiconic.domoticzapp.api

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.IBinder
import android.util.Log
import androidx.preference.PreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit

class DomoticzWebSocketService(
    private val context: Context,
    private val serverUrl: String,
    private val alarmHandler: AlarmPushHandler
) : Service() {
    private val client = OkHttpClient.Builder()
        .pingInterval(30, TimeUnit.SECONDS)
        .build()

    private val reconnectDelayMillis = 5000L
    private val maxReconnectAttempts = 5

    private var reconnectAttempts = 0
    private var isConnecting = false

    private var webSocket: WebSocket? = null

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    init {
        connect()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_PROXIMITY_TRIGGER -> sendProximityTrigger()
            }
        }
        return START_NOT_STICKY
    }

    private fun sendProximityTrigger() {
        val triggerMessage = "{\"type\":\"proximity\",\"action\":\"trigger\"}"
        sendMessage(triggerMessage)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    fun connect() {
        if (isConnecting || webSocket != null) return
        isConnecting = true

        Log.d(TAG, "WebSocket URL: $serverUrl")
        val request = Request.Builder().url(serverUrl).build()
        webSocket = client.newWebSocket(request, createWebSocketListener())
    }

    private fun createWebSocketListener() = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            isConnecting = false
            reconnectAttempts = 0
            _connectionState.value = ConnectionState.Connected
            Log.d(TAG, "WebSocket connection established")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            alarmHandler.handleIncomingPush(text)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.e(TAG, "WebSocket failure: ${t.message}")
            handleConnectionFailure()
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Log.d(TAG, "WebSocket closed: $reason")
            handleConnectionClosed()
        }
    }

    private fun handleConnectionFailure() {
        isConnecting = false
        _connectionState.value = ConnectionState.Disconnected

        if (reconnectAttempts < maxReconnectAttempts) {
            reconnectAttempts++
            Log.d(TAG, "Attempting to reconnect (attempt $reconnectAttempts)")
            Handler(Looper.getMainLooper()).postDelayed({ connect() }, reconnectDelayMillis)
        } else {
            Log.e(TAG, "Max reconnection attempts reached")
        }
    }

    private fun handleConnectionClosed() {
        isConnecting = false
        webSocket = null
        _connectionState.value = ConnectionState.Disconnected
    }

    fun sendMessage(message: String): Boolean {
        return webSocket?.send(message) ?: false
    }

    fun disconnect() {
        webSocket?.close(1000, "Closing connection")
        webSocket = null
        isConnecting = false
        _connectionState.value = ConnectionState.Disconnected
    }

    sealed class ConnectionState {
        object Connected : ConnectionState()
        object Disconnected : ConnectionState()
    }

    companion object {
        private const val TAG = "DomoticzWebSocket"
        const val ACTION_PROXIMITY_TRIGGER = "com.wiconic.domoticzapp.PROXIMITY_TRIGGER"
    }
}
