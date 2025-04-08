package com.wiconic.domoticzapp.controller

import android.util.Log
import android.widget.Button

class GateController(
    private val sendMessage: (String) -> Unit
    ) {
    private val TAG = "GateController"

    fun setGateButton(button: Button) {
        button.setOnClickListener { openGate() }
    }

    fun openGate() {
        val message = "{\"type\": \"openGateCommand\"}"
        sendMessage(message)
        Log.d(TAG, "Gate open request sent.")
    }
}
