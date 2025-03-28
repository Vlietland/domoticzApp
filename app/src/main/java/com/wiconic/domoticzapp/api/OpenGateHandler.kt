package com.wiconic.domoticzapp.api

import android.content.Context
import android.util.Log
import kotlin.jvm.Throws
import org.json.JSONObject

interface GateOperationCallback {
    fun onGateOperationSuccess()
    fun onGateOperationFailure(errorMessage: String)
}

class OpenGateHandler(
    private val context: Context,
    private val webSocketService: DomoticzWebSocketService
) {
    private var callback: GateOperationCallback? = null

    fun setCallback(callback: GateOperationCallback) {
        this.callback = callback
    }

    fun openGate() {
        val payload = JSONObject()
        payload.put("type", "opengate")
        webSocketService.sendMessage(payload.toString())
        Log.i("OpenGateHandler", "Gate open command sent")
    }
}
