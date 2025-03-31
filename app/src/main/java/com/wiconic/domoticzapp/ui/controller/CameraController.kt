package com.wiconic.domoticzapp.ui.controller

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.wiconic.domoticzapp.api.DomoticzAppServerConnection

class CameraController(
    val context: Context,
    private val cameraImageView: ImageView,
    private val swipeRefreshLayout: SwipeRefreshLayout,
    private val serverConnection: DomoticzAppServerConnection
) {

    private var currentCameraIndex = 0
    private val cameraIds = listOf(
        "garage", "pantry", "frontdoorentry", "frontdoor",
        "gardensouth", "terraceliving", "gardenwest", "backdoor"
    )

    init {
        swipeRefreshLayout.setOnRefreshListener { onImageLoading() }
    }

    fun loadNextImage() {
        if (currentCameraIndex < cameraIds.size - 1) currentCameraIndex++ else currentCameraIndex = 0
        loadCameraImage()
    }

    fun loadPreviousImage() {
        if (currentCameraIndex > 0) currentCameraIndex-- else currentCameraIndex = cameraIds.size - 1
        loadCameraImage()
    }

    fun loadNewImageFromCurrentCamera() {
        loadCameraImage()
    }

    fun loadCameraImage() {
        val cameraId = cameraIds[currentCameraIndex]
        val message = "{\"type\": \"getCameraImage\", \"cameraId\": \"$cameraId\"}"
        serverConnection.sendMessage(message)
        onImageLoading()
    }

    fun handleIncomingImage(imageData: String) {
        displayImage(imageData)
    }

    private fun onImageLoading() {
        swipeRefreshLayout.isRefreshing = true
        Log.d(TAG, "Image loading started.")
    }

    private fun displayImage(imageData: String) {
        swipeRefreshLayout.isRefreshing = false
        Log.d(TAG, "Image displayed successfully.")

        try {
            Log.d(TAG, "Image data size: ${imageData.length}")
            val decodedBytes = Base64.decode(imageData, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            if (bitmap != null) {
                cameraImageView.setImageBitmap(bitmap)
                Log.d(TAG, "Bitmap successfully displayed.")
            } else {
                Log.e(TAG, "Failed to decode bitmap.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to decode image: ${e.message}")
        }
    }

    companion object {
        private const val TAG = "CameraController"
    }
}
