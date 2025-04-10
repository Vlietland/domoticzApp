package com.wiconic.domoticzapp.controller

import android.widget.ImageView
import android.util.Log
import com.wiconic.domoticzapp.R
import com.wiconic.domoticzapp.model.AppPreferences

class ServerIconController() {
    private val TAG = "ServerIconController"  
    private var isConnected = false
    private var serverConnectionIcon: ImageView? = null
    private val ICON_CONNECTED = R.drawable.ic_server_connection_active
    private val ICON_DISCONNECTED = R.drawable.ic_server_connection_deactive

    fun setServerConnectionIcon(icon: ImageView) {
        serverConnectionIcon = icon
    }

    fun onWebSocketActiveListeners(isConnected: Boolean) {
        this.isConnected = isConnected
        Log.d(TAG, "Server connection icon refreshing with server status: ${isConnected}.")                        
        updateServerConnectionIcon()
    }

    fun updateServerConnectionIcon() {
        if (serverConnectionIcon == null) return  
        Log.d(TAG, "Server connection icon refreshing with server status: ${isConnected}.")              
        val newIconImage = if (isConnected) ICON_CONNECTED else ICON_DISCONNECTED
        serverConnectionIcon?.setImageResource(newIconImage)
    }
}
