package com.wiconic.domoticzapp.controller

import android.content.Context
import android.widget.ImageView
import com.wiconic.domoticzapp.R
import com.wiconic.domoticzapp.model.AppPreferences

class ServerIconController(
    private val context: Context,
    private val serverConnectionIcon: ImageView
) {
    private var isConnected = false
    private val ICON_CONNECTED = R.drawable.ic_server_connection_active
    private val ICON_DISCONNECTED = R.drawable.ic_server_connection_deactive

    init {
        updateServerConnectionIcon()
    }

    fun onWebSocketActiveListeners(isConnected: Boolean) {
        this.isConnected = isConnected
        updateServerConnectionIcon()
    }

    private fun updateServerConnectionIcon() {
        val icon = if (isConnected) ICON_CONNECTED else ICON_DISCONNECTED
        serverConnectionIcon.setImageResource(icon)
    }
}
