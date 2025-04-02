package com.wiconic.domoticzapp.ui.controller

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import com.wiconic.domoticzapp.api.DomoticzAppServerConnection

class CameraController(
    val context: Context,
    private val cameraImageView: ImageView,
    private val serverConnection: DomoticzAppServerConnection
) {

    private var currentCameraIndex = 0
    private val cameraIds = listOf(
        "Garage", "Pantry", "FrontdoorEntry", "Frontdoor",
        "GardenSouth", "TerraceLiving", "GardenWest", "Backdoor"
    )

    init {
        Log.d(TAG, "CameraController initialized with ${cameraIds.size} cameras.")
    }

    fun loadNextImage() {
        currentCameraIndex = if (currentCameraIndex < cameraIds.size - 1) currentCameraIndex + 1 else 0
        Log.d(TAG, "Swiping to next image. Current camera index: $currentCameraIndex, Camera ID: ${cameraIds[currentCameraIndex]}")
        loadCameraImage()
    }

    fun loadPreviousImage() {
        currentCameraIndex = if (currentCameraIndex > 0) currentCameraIndex - 1 else cameraIds.size - 1
        Log.d(TAG, "Swiping to previous image. Current camera index: $currentCameraIndex, Camera ID: ${cameraIds[currentCameraIndex]}")
        loadCameraImage()
    }

    fun loadNewImageFromCurrentCamera() {
        Log.d(TAG, "Refreshing current camera image. Camera ID: ${cameraIds[currentCameraIndex]}")
        loadCameraImage()
    }

    fun loadCameraImage() {
        val cameraId = cameraIds[currentCameraIndex]
        val message = "{\"type\": \"getCameraImage\", \"cameraId\": \"$cameraId\"}"
        
        Log.d(TAG, "Requesting camera image for Camera ID: $cameraId with message: $message")
        serverConnection.sendMessage(message)
        
        displayImageLoading()
    }

    fun handleIncomingImage(imageData: String) {
        Log.d(TAG, "Received image data. Length: ${imageData.length}")
        displayImage(imageData)
    }

    private fun displayImageLoading() {
        Log.d(TAG, "Image loading started.")
    }

    private fun displayImage(imageData: String) {
        Log.d(TAG, "Attempting to decode and display image.")

        try {
            Log.d(TAG, "Image data size: ${imageData.length}")
            val decodedBytes = Base64.decode(imageData, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            
            if (bitmap != null) {
                cameraImageView.setImageBitmap(bitmap)
                Log.d(TAG, "Bitmap successfully displayed on ImageView.")
            } else {
                Log.e(TAG, "Failed to decode bitmap. Bitmap is null.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to decode image: ${e.message}")
        }
    }

    companion object {
        private const val TAG = "CameraController"
    }
}
