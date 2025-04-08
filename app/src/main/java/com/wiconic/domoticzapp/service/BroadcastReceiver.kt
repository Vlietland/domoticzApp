package com.wiconic.domoticzapp.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            val serviceIntent = Intent(context, DomoticzAppService::class.java)
            context.startService(serviceIntent)
            Log.d("BootBroadcastReceiver", "DomoticzAppService started after reboot")
        }
    }
}
