package com.wiconic.domoticzapp.controller

import android.widget.ImageView
import com.wiconic.domoticzapp.R
import com.wiconic.domoticzapp.model.AppPreferences

class ServerIconController() {
    private var isConnected = false
    private var serverConnectionIcon: ImageView? = null
    private val ICON_CONNECTED = R.drawable.ic_server_connection_active
    private val ICON_DISCONNECTED = R.drawable.ic_server_connection_deactive

    fun setServerConnectionIcon(icon: ImageView) {
        serverConnectionIcon = icon
        updateServerConnectionIcon()
    }

    fun onWebSocketActiveListeners(isConnected: Boolean) {
        this.isConnected = isConnected
        updateServerConnectionIcon()
    }

    private fun updateServerConnectionIcon() {
        if (serverConnectionIcon == null) return        
        val newIconImage = if (isConnected) ICON_CONNECTED else ICON_DISCONNECTED
        serverConnectionIcon?.setImageResource(newIconImage)
    }
}
