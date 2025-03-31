package com.wiconic.domoticzapp.ui.controller

import android.util.Log
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TextController(
    private val messagesTextView: TextView
) : ViewModel() {

    private val _messages = MutableLiveData<List<String>>(emptyList())
    val messages: LiveData<List<String>> = _messages

    fun addMessage(message: String) {
        CoroutineScope(Dispatchers.Main).launch { // Ensure UI update happens on the main thread
            val currentMessages = _messages.value?.toMutableList() ?: mutableListOf()
            currentMessages.add(message)
            _messages.value = currentMessages.toList()
            
            messagesTextView.text = currentMessages.joinToString("\n")
            Log.i(TAG, "Message added: $message")
        }
    }
    companion object {
        private const val TAG = "TextController"
    }
}
