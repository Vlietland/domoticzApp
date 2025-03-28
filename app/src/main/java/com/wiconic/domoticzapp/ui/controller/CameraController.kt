package com.wiconic.domoticzapp.ui.controller

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.wiconic.domoticzapp.api.AlarmPushHandler
import com.wiconic.domoticzapp.api.ImageFetchCallback
import com.wiconic.domoticzapp.api.ImageFetchHandler
import org.json.JSONObject

class CameraController(
    private val context: Context,
    private val cameraImageView: ImageView,
    private val swipeRefreshLayout: SwipeRefreshLayout,
    private val alarmPushHandler: AlarmPushHandler
) : ImageFetchCallback {
    var currentCameraIndex = 0
    private val imageFetchHandler = alarmPushHandler.webSocketService?.let {
        ImageFetchHandler(context, it)
    }
    
    // List of camera IDs - hardcoded to match server configuration
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

    init {
        swipeRefreshLayout.setOnRefreshListener { loadCameraImage() }
        imageFetchHandler?.let {
            for (cameraId in cameraIds) {
                it.registerCallback(cameraId, this)
            }
        }
    }

    fun loadCameraImage() {
        if (currentCameraIndex >= cameraIds.size) {
            showToast("Invalid camera index")
            swipeRefreshLayout.isRefreshing = false
            return
        }
        
        val cameraId = cameraIds[currentCameraIndex]
        
        // Use ImageFetchHandler if available, otherwise fall back to direct WebSocket request
        if (imageFetchHandler != null) {
            imageFetchHandler.fetchCameraImage(cameraId)
        } else {
            // Fall back to direct WebSocket request
            val message = JSONObject().apply {
                put("type", "getCameraImage")
                put("cameraId", cameraId)
            }.toString()
            
            alarmPushHandler.sendMessage(message)
        }
        
        swipeRefreshLayout.isRefreshing = true
    }

    fun updateAlarmUI(imageData: String?) {
        if (!imageData.isNullOrEmpty()) {
            try {
                val imageBytes = Base64.decode(imageData, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                updateImageView(bitmap)
            } catch (e: Exception) {
                cameraImageView.visibility = View.GONE
                showToast("Error decoding image: ${e.message}")
            }
        } else {
            cameraImageView.visibility = View.GONE
        }
    }
    
    private fun updateImageView(bitmap: Bitmap?) {
        if (bitmap != null) {
            cameraImageView.visibility = View.VISIBLE
            cameraImageView.setImageBitmap(bitmap)
            val params = cameraImageView.layoutParams
            params.height = (context.resources.displayMetrics.heightPixels * 0.33).toInt()
            cameraImageView.layoutParams = params
        } else {
            cameraImageView.visibility = View.GONE
        }
    }
    
    override fun onImageFetched(cameraId: String, bitmap: Bitmap?) {
        swipeRefreshLayout.isRefreshing = false
        updateImageView(bitmap)
    }
    
    override fun onImageFetchError(cameraId: String, errorMessage: String) {
        swipeRefreshLayout.isRefreshing = false
        showToast("Error loading camera image: $errorMessage")
        cameraImageView.visibility = View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
