package com.wiconic.domoticzapp.controller

import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.wiconic.domoticzapp.BuildConfig

class CameraController(private val sendMessage: (String) -> Unit
    ) {
    private var cameraImageView: ImageView? = null
    private var cameraProgressBar: ProgressBar? = null
    private var currentCameraIndex = 1
    private var maxCameras = BuildConfig.MAX_CAMERAS
    private val TAG = "CameraController"        

    init {
        Log.v(TAG, "CameraController initialized with ${maxCameras} cameras.")
    }

    fun setImageViewAndProgress(newImageView: ImageView, newProgressBar: ProgressBar) {
        cameraImageView = newImageView
        cameraProgressBar = newProgressBar
        loadNewImageFromCurrentCamera()
    }

    fun loadNextImage() {
        currentCameraIndex = if (currentCameraIndex < maxCameras) currentCameraIndex + 1 else 1
        Log.v(TAG, "Swiping to next image. Current camera index: $currentCameraIndex")
        loadCameraImage()
    }

    fun loadPreviousImage() {
        currentCameraIndex = if (currentCameraIndex > 1) currentCameraIndex - 1 else maxCameras
        Log.v(TAG, "Swiping to previous image. Current camera index: $currentCameraIndex")
        loadCameraImage()
    }

    fun loadNewImageFromCurrentCamera() {
        Log.v(TAG, "Refreshing current camera image. Camera ID: $currentCameraIndex")
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

    private fun loadCameraImage() {
        val message = "{\"type\": \"getCameraImage\", \"cameraId\": \"$currentCameraIndex\"}"
        Log.v(TAG, "Requesting camera image for Camera ID: $currentCameraIndex with message: $message")
        sendMessage(message)
        displayImageLoading()
    }

    fun onImage(imageData: String) {
        Log.v(TAG, "Received image data. Length: ${imageData.length}")
        Log.v(TAG, "Attempting to decode and display image.")
        if (cameraImageView == null) return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.v(TAG, "Image data size: ${imageData.length}")
                val decodedBytes = Base64.decode(imageData, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                if (bitmap != null) {
                    Log.d(TAG, "Bitmap successfully decoded.")
                    launch(Dispatchers.Main) {
                        cameraProgressBar?.visibility = View.GONE
                        cameraImageView?.visibility = View.VISIBLE
                        cameraImageView?.setImageBitmap(bitmap)
                        Log.v(TAG, "Bitmap successfully displayed on ImageView.")
                    }
                } else {
                    Log.e(TAG, "Failed to decode bitmap. Bitmap is null.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to decode image: ${e.message}")
            }
        }
    }

    fun onWebSocketActiveListeners(active: Boolean)
    {   
        if (active) loadNewImageFromCurrentCamera()
    }

    private fun displayImageLoading() {
        Log.v(TAG, "Image loading started.")
        Handler(Looper.getMainLooper()).post {
            cameraProgressBar?.visibility = View.VISIBLE
        }
    }
}
