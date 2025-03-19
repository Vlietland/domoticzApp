package com.wiconic.domoticzapp.api

import android.util.Log
import com.wiconic.domoticzapp.ui.MainActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class AlarmPushHandler {
    private val client = OkHttpClient()
    private var mainActivity: MainActivity? = null

    // Set the MainActivity reference for UI updates
    fun setMainActivity(activity: MainActivity) {
        this.mainActivity = activity
    }

    // Handle incoming push notifications
    fun handleIncomingPush(jsonPayload: String) {
        try {
            val json = JSONObject(jsonPayload)
            val alarmText = json.optString("alarm", "Unknown alarm")
            val imageUrl = json.optString("imageUrl", null)
            
            // Update the UI through MainActivity
            mainActivity?.handleAlarmNotification(alarmText, if (imageUrl.isNullOrEmpty()) null else imageUrl)
            
            Log.i("AlarmPushHandler", "Received alarm: $alarmText")
        } catch (e: Exception) {
            Log.e("AlarmPushHandler", "Error parsing push notification: ${e.message}")
        }
    }

    // Send a push request to the server
    fun handlePushRequest(url: String, jsonPayload: String, imageUrl: String? = null) {
        val requestBody = jsonPayload.toRequestBody("application/json; charset=utf-8".toMediaType())
        val requestBuilder = Request.Builder()
            .url(url)
            .post(requestBody)

        // If an image URL is provided, add it to the request headers
        imageUrl?.let {
            requestBuilder.addHeader("Image-URL", it)
        }

        val request = requestBuilder.build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("AlarmPushHandler", "Failed to send push request: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.i("AlarmPushHandler", "Push request successful: ${response.body?.string()}")
                    
                    // For demo purposes, simulate receiving the push notification we just sent
                    val responseJson = JSONObject().apply {
                        put("alarm", "Alarm triggered")
                        imageUrl?.let { put("imageUrl", it) }
                    }.toString()
                    
                    handleIncomingPush(responseJson)
                } else {
                    Log.e("AlarmPushHandler", "Push request failed: ${response.code}")
                }
            }
        })
    }
}
