package com.wiconic.domoticzapp.ui.controller

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Log

class TextController : ViewModel() {

    private val _messages = MutableLiveData<List<String>>(emptyList())
    val messages: LiveData<List<String>> = _messages

    fun addMessage(message: String) {
        val currentMessages = _messages.value?.toMutableList() ?: mutableListOf()
        currentMessages.add(message)
        _messages.value = currentMessages.toList()
        Log.i(TAG, "Message added: $message")
    }

    companion object {
        private const val TAG = "TextController"
    }
}
