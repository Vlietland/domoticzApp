package com.wiconic.domoticzapp.connectivity

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.wiconic.domoticzapp.R

class SoundConnector(context: Context) {
    private val tag = "SoundConnector"
    private val channelId = "DOMOTICZ_ALERT_CHANNEL"
    private val channelName = "Domoticz Alerts"

    private val appContext = context.applicationContext
    private val polluxUri = Uri.parse("android.resource://${appContext.packageName}/raw/pollux")
    private val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(channelId) == null) {
                val channel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    setSound(null, null)
                    enableVibration(true)
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                }
                notificationManager.createNotificationChannel(channel)
                Log.d(tag, "Notification channel initialized")
            }
        }
    }

    fun playNotification(deviceName: String) {
        val notification = NotificationCompat.Builder(appContext, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("🚨 Domoticz Alert")
            .setContentText("Triggered: $deviceName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(deviceName.hashCode(), notification)

        try {
            val player = MediaPlayer().apply {
                setDataSource(appContext, polluxUri)
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                setOnCompletionListener {
                    it.release()
                    Log.d(tag, "MediaPlayer released")
                }
                prepare()
                start()
            }
            Log.d(tag, "Pollux sound played via MediaPlayer")
        } catch (e: Exception) {
            Log.e(tag, "Failed to play sound via MediaPlayer: ${e.message}")
        }
    }
}
