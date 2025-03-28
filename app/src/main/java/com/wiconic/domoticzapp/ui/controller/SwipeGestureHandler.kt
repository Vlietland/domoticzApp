package com.wiconic.domoticzapp.ui.controller

import android.view.MotionEvent
import android.view.View
import android.widget.ImageView

class SwipeGestureHandler(
    private val cameraController: CameraController
) {

    fun handleSwipe(imageView: ImageView, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                imageView.tag = Pair(event.x, event.y)
                return true
            }
            MotionEvent.ACTION_UP -> {
                val (initialX, initialY) = imageView.tag as? Pair<Float, Float> ?: return false
                val deltaX = event.x - initialX
                val deltaY = event.y - initialY
                val swipeThreshold = 100
                val swipeVelocityThreshold = 100
                if (Math.abs(deltaX) > swipeThreshold && Math.abs(deltaX) > swipeVelocityThreshold) {
                    cameraController.currentCameraIndex = if (deltaX > 0) (cameraController.currentCameraIndex - 1 + cameraController.cameraIds.size) % cameraController.cameraIds.size else (cameraController.currentCameraIndex + 1) % cameraController.cameraIds.size
                    cameraController.loadCameraImage()
                } else if (Math.abs(deltaY) > swipeThreshold && Math.abs(deltaY) > swipeVelocityThreshold) {
                    cameraController.loadCameraImage()
                }
                return true
            }
            else -> {
                return false
            }
        }
    }
}
