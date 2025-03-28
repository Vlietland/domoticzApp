package com.wiconic.domoticzapp.ui.controller

import android.content.Context
import android.widget.Toast
import com.wiconic.domoticzapp.R
import com.wiconic.domoticzapp.api.DomoticzWebSocketService
import com.wiconic.domoticzapp.api.GateOperationCallback
import com.wiconic.domoticzapp.api.OpenGateHandler

class GateController(
    private val context: Context,
    private val webSocketService: DomoticzWebSocketService
) {

    fun openGate(callback: GateOperationCallback) {
        try {
            val openGateHandler = OpenGateHandler(context, webSocketService)
            openGateHandler.setCallback(callback)
            openGateHandler.openGate()
            showToast(context.getString(R.string.gate_opening))
        } catch (e: Exception) {
            showToast(context.getString(R.string.gate_error))
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
