package com.wiconic.domoticzapp.ui.controller

import android.util.Log
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale  
import com.wiconic.domoticzapp.BuildConfig

class TextController(private var messagesTextView: TextView) : ViewModel() {
    private val _messages = MutableLiveData<List<String>>(emptyList())
    val messages: LiveData<List<String>> = _messages
    private var storedMessages: MutableList<String> = mutableListOf()    

    private val deviceToMessageMap = mapOf(
        BuildConfig.DEVICE_1 to BuildConfig.DEVICE_1_MESSAGE,
        BuildConfig.DEVICE_2 to BuildConfig.DEVICE_2_MESSAGE,
        BuildConfig.DEVICE_3 to BuildConfig.DEVICE_3_MESSAGE,
        BuildConfig.DEVICE_4 to BuildConfig.DEVICE_4_MESSAGE,
        BuildConfig.DEVICE_5 to BuildConfig.DEVICE_5_MESSAGE,
    )

    init {
        _messages.value = storedMessages
        Log.i(TAG, "testiTESTESTSTEng ${BuildConfig.DEVICE_1_MESSAGE}")
        Log.i(TAG, "testiTESTESTSTEng ${BuildConfig.DEVICE_2_MESSAGE}")
        Log.i(TAG, "testiTESTESTSTEng ${BuildConfig.DEVICE_3_MESSAGE}")
        Log.i(TAG, "testiTESTESTSTEng ${BuildConfig.DEVICE_4_MESSAGE}")
        Log.i(TAG, "testiTESTESTSTEng ${BuildConfig.DEVICE_5_MESSAGE}")
        updateTextView()
    }

    fun updateTextView(newTextView: TextView) {
        messagesTextView = newTextView
    }

    fun clearMessages() {
        storedMessages.clear()
        _messages.value = storedMessages.toList()
        updateTextView()
        Log.i(TAG, "All messages cleared")
    }

    fun getMessages(): String {
        return storedMessages.joinToString("\n")
    }

    fun addMessage(deviceName: String) {
        val message = deviceToMessageMap[deviceName]
        if (message != null) {
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val formattedMessage = "[$timestamp] $message"
            storedMessages.add(formattedMessage)
            _messages.value = storedMessages.toList()
            updateTextView()
            Log.i(TAG, "Message added for device: $deviceName - $formattedMessage")
        } else {
            Log.w(TAG, "Unknown device name: $deviceName")
        }
    }

    private fun updateTextView() {
        messagesTextView.text = getMessages()
    }

    companion object {
        private const val TAG = "TextController"
    }    
}
