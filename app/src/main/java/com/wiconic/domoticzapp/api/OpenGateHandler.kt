package com.wiconic.domoticzapp.api

import android.util.Log
import okhttp3.*
import java.io.IOException
import kotlin.jvm.Throws

interface GateOperationCallback {
    fun onGateOperationSuccess()
    fun onGateOperationFailure(errorMessage: String)
}

class OpenGateHandler {
    private val client = OkHttpClient()
    private var callback: GateOperationCallback? = null

    fun setCallback(callback: GateOperationCallback) {
        this.callback = callback
    }

    fun openGate() {
        val url = "https://domoticz-server.com/api/open-gate" // Replace with actual URL
        val request = Request.Builder()
            .url(url)
            .post(RequestBody.create(null, ByteArray(0))) // Empty POST request
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                val errorMessage = "Failed to open gate: ${e.message}"
                Log.e("OpenGateHandler", errorMessage)
                callback?.onGateOperationFailure(errorMessage)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.i("OpenGateHandler", "Gate opened successfully")
                    callback?.onGateOperationSuccess()
                } else {
                    val errorMessage = "Failed to open gate: ${response.code}"
                    Log.e("OpenGateHandler", errorMessage)
                    callback?.onGateOperationFailure(errorMessage)
                }
            }
        })
    }
}
