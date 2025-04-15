package com.wiconic.domoticzapp.controller

import android.widget.ImageView
import android.util.Log
import com.wiconic.domoticzapp.R
import com.wiconic.domoticzapp.model.AppPreferences
import com.wiconic.domoticzapp.connectivity.AppServerConnector

class ServerIconController(appServerConnector: AppServerConnector) {
    private val TAG = "ServerIconController"  
    private var isConnected = appServerConnector.isConnected()
    private var serverConnectionIcon: ImageView? = null    
    private val ICON_CONNECTED = R.drawable.ic_server_connection_active
    private val ICON_DISCONNECTED = R.drawable.ic_server_connection_deactive

    fun setServerConnectionIconView(icon: ImageView) {
        serverConnectionIcon = icon
        updateServerConnectionIcon()        
    }

    fun onWebSocketActiveListeners(isConnected: Boolean) {
        this.isConnected = isConnected
        Log.v(TAG, "Server connection icon refreshing with server status: ${isConnected}.") 
        updateServerConnectionIcon()        
    }

    fun updateServerConnectionIcon() {
        if (serverConnectionIcon == null) return  
        Log.v(TAG, "Server connection icon refreshing with server status: ${isConnected}.")              
        val newIconImage = if (isConnected) ICON_CONNECTED else ICON_DISCONNECTED
        serverConnectionIcon?.setImageResource(newIconImage)
    }
}
