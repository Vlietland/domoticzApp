package com.wiconic.domoticzapp.ui.controller

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import com.wiconic.domoticzapp.BuildConfig
import com.wiconic.domoticzapp.api.DomoticzAppServerConnection

class CameraController(
    private val context: Context,
    private var cameraImageView: ImageView,
    private val domoticzAppServerConnection: DomoticzAppServerConnection) {
    private var currentCameraIndex = 1
    private var maxCameras = BuildConfig.MAX_CAMERAS
    private var TAG = "CameraController"

    init {
        Log.d(TAG, "CameraController initialized with ${maxCameras} cameras.")
    }

    fun setImageView(newImageView: ImageView) {
        cameraImageView = newImageView
    }

    fun loadNextImage() {
        currentCameraIndex = if (currentCameraIndex < maxCameras) currentCameraIndex + 1 else 1
        Log.d(TAG, "Swiping to next image. Current camera index: $currentCameraIndex, Camera ID: ${currentCameraIndex}")
        loadCameraImage()
    }

    fun loadPreviousImage() {
        currentCameraIndex = if (currentCameraIndex > 1) currentCameraIndex - 1 else maxCameras
        Log.d(TAG, "Swiping to previous image. Current camera index: $currentCameraIndex, Camera ID: $(currentCameraIndex}")
        loadCameraImage()
    }

    fun loadNewImageFromCurrentCamera() {
        Log.d(TAG, "Refreshing current camera image. Camera ID: $(currentCameraIndex}")
        loadCameraImage()
    }

    fun setCurrentCamera(deviceName: String) {
        if (deviceName == BuildConfig.DEVICE_1) {
            currentCameraIndex = 1
            loadCameraImage()
        } else if (deviceName == BuildConfig.DEVICE_2) {
            currentCameraIndex = 4
            loadCameraImage()
        }
    }

    fun loadCameraImage() {
        val message = "{\"type\": \"getCameraImage\", \"cameraId\": \"$currentCameraIndex\"}"
        Log.d(TAG, "Requesting camera image for Camera ID: $currentCameraIndex with message: $message")
        domoticzAppServerConnection.sendMessage(message)
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
}
