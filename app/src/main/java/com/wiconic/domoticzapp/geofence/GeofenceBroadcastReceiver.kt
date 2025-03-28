package com.wiconic.domoticzapp.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.wiconic.domoticzapp.api.DomoticzWebSocketService

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            Log.d("GeofenceReceiver", "Proximity trigger received")
            val webSocketIntent = Intent(context, DomoticzWebSocketService::class.java)
            webSocketIntent.action = DomoticzWebSocketService.ACTION_PROXIMITY_TRIGGER
            context.startService(webSocketIntent)
        }
    }
}
