package com.wiconic.domoticzapp.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class DomoticzPreferenceManager(context: Context) {
    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun getServerIp(): String {
        return prefs.getString(SERVER_IP_KEY, DEFAULT_SERVER_IP) ?: DEFAULT_SERVER_IP
    }

    fun getServerPort(): Int {
        return prefs.getString(SERVER_PORT_KEY, DEFAULT_SERVER_PORT)?.toIntOrNull() ?: DEFAULT_SERVER_PORT.toInt()
    }

    fun getWebSocketUrl(): String {
        return "ws://${getServerIp()}:${getServerPort()}/websocket"
    }

    companion object {
        const val SERVER_IP_KEY = "server_ip"
        const val SERVER_PORT_KEY = "server_port"
        private const val DEFAULT_SERVER_IP = "192.168.0.1"
        private const val DEFAULT_SERVER_PORT = "8000"
    }
}
