package com.wiconic.domoticzapp.ui.controller

import android.util.Log
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TextController(private val messagesTextView: TextView) : ViewModel() {
    private val _messages = MutableLiveData<List<String>>(emptyList())
    val messages: LiveData<List<String>> = _messages
    private var storedMessages: MutableList<String> = mutableListOf()    

    init {
        _messages.value = storedMessages
        updateTextView()
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

    fun addMessage(message: String) {
        val currentMessages = _messages.value?.toMutableList() ?: mutableListOf()
        currentMessages.add(message)
        _messages.value = currentMessages.toList()
        
        messagesTextView.text = currentMessages.joinToString("\n")
        Log.i(TAG, "Message added: $message")
    }

    private fun updateTextView() {
        messagesTextView.text = getMessages()
    }

    companion object {
        private const val TAG = "TextController"
    }
}
