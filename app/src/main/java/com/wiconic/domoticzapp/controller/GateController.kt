package com.wiconic.domoticzapp.controller

import android.util.Log
import android.widget.Button

class GateController(
    private val sendMessage: (String) -> Unit
    ) {
    private val TAG = "GateController"

    fun setGateButtons(openButton: Button, closeButton: Button) {
        openButton.setOnClickListener { openGate() }
        closeButton.setOnClickListener { closeGate() }
    }

    fun openGate() {
        val message = "{\"type\": \"openGateCommand\"}"
        sendMessage(message)
        Log.d(TAG, "Gate open request sent.")
    }

    fun closeGate() {
        val message = "{\"type\": \"closeGateCommand\"}"
        sendMessage(message)
        Log.i(TAG, "Gate close request sent.")
    }
}
