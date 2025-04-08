package com.wiconic.domoticzapp.service

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
import android.util.Log
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import com.wiconic.domoticzapp.R
import com.wiconic.domoticzapp.connectivity.AppServerConnector
import com.wiconic.domoticzapp.model.AppPreferences
import com.wiconic.domoticzapp.service.MessageHandler

class DomoticzAppService : Service () {
    private val TAG = "DomoticzAppService"
    private lateinit var appServerConnector: AppServerConnector
    private lateinit var appPreferences: AppPreferences
    private var onServiceCreatedCallback: (() -> Unit)? = null
    private val messageHandler = MessageHandler(this)
    private val binder = LocalBinder()
    private var isBound = false

    inner class LocalBinder : Binder() {
        fun getService(): DomoticzAppService = this@DomoticzAppService
    }

    fun setOnServiceCreatedCallback(callback: () -> Unit) {
        onServiceCreatedCallback = callback
    }

    fun getMessageHandler(): MessageHandler = messageHandler
    fun getAppServerConnector(): AppServerConnector = appServerConnector
    fun getAppPreferences(): AppPreferences = appPreferences
    fun isServiceBound() = isBound

    override fun onCreate() {
        super.onCreate()
        appPreferences = AppPreferences(this)
        appServerConnector = AppServerConnector(appPreferences)
        appServerConnector.setOnMessageReceivedCallback(messageHandler::onMessageReceived)
        appServerConnector.initializeConnection()
        appServerConnector.addOnWebSocketActiveListener { isActive ->
            Log.i(TAG, "WebSocket is active: $isActive")
        }
        Log.i(TAG, "WebSocketService started and WebSocket initialized")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundService()
        Log.d(TAG, "Foreground Service started successfully")
        return START_STICKY
    }

    private fun startForegroundService() {
        val channelId = "DOMOTICZ_APP_CHANNEL"
        val channelName = "Domoticz App Channel"
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Domoticz App")
            .setContentText("WebSocket Connection Active")
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(1, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        appServerConnector.disconnect()
        Log.i(TAG, "WebSocketService stopped and WebSocket disconnected")
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.d("DomoticzAppService", "Service bound successfully")
        return binder
    }
}
