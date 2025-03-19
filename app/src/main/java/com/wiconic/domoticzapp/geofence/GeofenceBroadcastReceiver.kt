package com.wiconic.domoticzapp.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("GeofenceReceiver", "Received geofence broadcast")
        // Geofence implementation will be added later
    }
}
