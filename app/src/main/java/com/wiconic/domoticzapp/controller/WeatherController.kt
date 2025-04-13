package com.wiconic.domoticzapp.controller

import android.util.Log
import android.widget.TextView

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
        if (temp.isNotEmpty()) {
            temperature = temp.toInt()
            updateTempView()
            Log.d(TAG, "Retrieved and published weatherdata")
        }
        else {
            Log.d(TAG, "No weatherdata available")        
        }
    }

    fun getWeather() {
        val message = "{\"type\": \"getWeather\"}"
        Log.d(TAG, "Requesting weather with message: $message")
        sendMessage(message)
    }

    private fun updateTempView() {
        temperatureView?.text = "$temperature°C"
    }
}
