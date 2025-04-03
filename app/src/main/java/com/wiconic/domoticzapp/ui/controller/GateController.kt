package com.wiconic.domoticzapp.ui.controller

import android.util.Log
import android.widget.Button
import com.wiconic.domoticzapp.api.DomoticzAppServerConnection

class GateController(
    private val domoticzAppServerConnection: DomoticzAppServerConnection,
    private val openGateButton: Button
) {

    init {
        openGateButton.setOnClickListener { openGate() }
    }

    fun openGate() {
        val message = """
            {
                "type": "opengate"
            }
        """.trimIndent()

        val isSent = domoticzAppServerConnection.sendMessage(message)
        if (isSent) {
            Log.d(TAG, "Gate open request sent successfully.")
        } else {
            Log.e(TAG, "Failed to send gate open request.")
        }
    }

    companion object {
        private const val TAG = "GateController"
    }
}
