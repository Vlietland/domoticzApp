package com.wiconic.domoticzapp.api

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import com.wiconic.domoticzapp.settings.DomoticzPreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.concurrent.ConcurrentHashMap

interface ImageFetchCallback {
    fun onImageFetched(cameraId: String, bitmap: Bitmap?)
    fun onImageFetchError(cameraId: String, errorMessage: String)
}

class ImageFetchHandler(
    private val context: Context,
    private val webSocketService: DomoticzWebSocketService
) {
    private val TAG = "ImageFetchHandler"
    private val callbacks = ConcurrentHashMap<String, ImageFetchCallback>()
    private val preferenceManager = DomoticzPreferenceManager(context)
    
    val cameraIds = listOf(
        "garage", 
        "pantry", 
        "frontdoorentry", 
        "frontdoor", 
        "gardensouth", 
        "terraceliving", 
        "gardenwest", 
        "backdoor"
    )
    
    fun registerCallback(cameraId: String, callback: ImageFetchCallback) {
        callbacks[cameraId] = callback
    }
    
    fun unregisterCallback(cameraId: String) {
        callbacks.remove(cameraId)
    }
    
    fun fetchCameraImage(cameraId: String) {
        if (!cameraIds.contains(cameraId)) {
            Log.e(TAG, "Invalid camera ID: $cameraId")
            callbacks[cameraId]?.onImageFetchError(cameraId, "Invalid camera ID")
            return
        }
        
        try {
            val payload = JSONObject().apply {
                put("type", "getCameraImage")
                put("cameraId", cameraId)
            }
            
            val success = webSocketService.sendMessage(payload.toString())
            if (!success) {
                Log.e(TAG, "Failed to send camera image request for $cameraId")
                callbacks[cameraId]?.onImageFetchError(cameraId, "Failed to send request")
            } else {
                Log.d(TAG, "Camera image request sent for $cameraId")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching camera image: ${e.message}")
            callbacks[cameraId]?.onImageFetchError(cameraId, "Error: ${e.message}")
        }
    }
    
    fun handleImageResponse(cameraId: String, imageData: String?) {
        if (imageData.isNullOrEmpty()) {
            Log.e(TAG, "Received empty image data for camera $cameraId")
            callbacks[cameraId]?.onImageFetchError(cameraId, "Empty image data received")
            return
        }
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val imageBytes = Base64.decode(imageData, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                
                withContext(Dispatchers.Main) {
                    callbacks[cameraId]?.onImageFetched(cameraId, bitmap)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error decoding image data: ${e.message}")
                withContext(Dispatchers.Main) {
                    callbacks[cameraId]?.onImageFetchError(cameraId, "Error decoding image: ${e.message}")
                }
            }
        }
    }
    
    fun loadImageIntoView(imageView: ImageView, bitmap: Bitmap) {
        imageView.setImageBitmap(bitmap)
    }
}
