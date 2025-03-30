package com.wiconic.domoticzapp.ui.controller

import android.content.Context
import com.wiconic.domoticzapp.api.DomoticzAppServerConnection
import com.wiconic.domoticzapp.ui.viewer.CameraImageViewer 

class CameraController(
    val context: Context,
    private val cameraImageViewer: CameraImageViewer,
    private val serverConnection: DomoticzAppServerConnection,
) {

    private var currentCameraIndex = 0
    private val cameraIds = listOf(
        "garage", "pantry", "frontdoorentry", "frontdoor",
        "gardensouth", "terraceliving", "gardenwest", "backdoor"
    )

    fun loadNextImage() {
        if (currentCameraIndex < cameraIds.size - 1) {
            currentCameraIndex++
        } else {
            currentCameraIndex = 0
        }
        loadCameraImage()
    }

    fun loadPreviousImage() {
        if (currentCameraIndex > 0) {
            currentCameraIndex--
        } else {
            currentCameraIndex = cameraIds.size - 1
        }
        loadCameraImage()
    }

    fun loadNewImageFromCurrentCamera() {
        loadCameraImage()
    }

    fun loadCameraImage() {
        val cameraId = cameraIds[currentCameraIndex]
        val message = "{\"type\": \"getCameraImage\", \"cameraId\": \"$cameraId\"}"
        serverConnection.sendMessage(message)

        // Notify the viewer that a new image is being loaded
        cameraImageViewer.onImageLoading()
    }

    fun handleIncomingImage(imageData: String) {
        cameraImageViewer.displayImage(imageData)
    }    
}
