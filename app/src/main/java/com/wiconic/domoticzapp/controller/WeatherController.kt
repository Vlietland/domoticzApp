package com.wiconic.domoticzapp.controller

import android.util.Log
import android.widget.TextView
import kotlin.math.roundToInt
import android.os.Handler
import android.os.Looper


class WeatherController(private val sendMessage: (String) -> Unit) {
    private val TAG = "WeatherController"
    private var temperature: Int = 0
    private var temperatureView: TextView? = null       

    fun setTemperatureView(view: TextView) {
        temperatureView = view
        getWeather()        
    }

    fun onWeatherDataReceived(temp: String) {
        Log.d(TAG, "Processed weather data: $temp")
        val value = temp.toDoubleOrNull()
        if (value != null) {
            temperature = value.roundToInt()
            updateTempView()
            Log.v(TAG, "Retrieved and published weatherdata")
        } else {
            Log.v(TAG, "No valid weather data available")
        }
    }

    fun onWebSocketActiveListeners(active: Boolean)
    {   
        if (active) getWeather()
    }

    fun getWeather() {
        val message = "{\"type\": \"getWeather\"}"
        Log.d(TAG, "Requesting weather with message: $message")
        sendMessage(message)
    }

    private fun updateTempView() {
        val tempString = "$temperature°"
        Log.d(TAG, "string changing to: $tempString")   
        Handler(Looper.getMainLooper()).post {
            temperatureView?.visibility = TextView.VISIBLE
            temperatureView?.text = tempString
        }
    }
}
