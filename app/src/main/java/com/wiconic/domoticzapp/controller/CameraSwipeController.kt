package com.wiconic.domoticzapp.controller

import android.content.Context
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat
import kotlin.math.abs

class CameraSwipeController(
    private val context: Context,
    private val cameraController: CameraController,
    private val cameraImageView: View
    
) : View.OnTouchListener {

    private val gestureDetector = GestureDetectorCompat(context, GestureListener())
    private val TAG = "CameraSwipeController"

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (v == null) return false

        val location = IntArray(2)
        cameraImageView.getLocationOnScreen(location)
        val x = event.rawX.toInt()
        val y = event.rawY.toInt()
        val viewRelativeX = x - location[0]
        val swipeEdgeLimit = (cameraImageView.width * 0.9).toInt()

        val isInside = viewRelativeX in 0 until swipeEdgeLimit &&
                       y >= location[1] && y <= location[1] + cameraImageView.height

        return if (isInside) {
            val result = gestureDetector.onTouchEvent(event)
            Log.v(TAG, "onTouch event received: ${event.action}, Result: $result")
            result
        } else {
            false
        }
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        private val SWIPE_THRESHOLD = 150
        private var lastActionTime: Long = 0
        private val MIN_ACTION_INTERVAL = 500L

        override fun onDown(e: MotionEvent): Boolean {
            Log.v(TAG, "onDown detected. Event: $e")
            return true
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            if (e1 == null) return false

            val location = IntArray(2)
            cameraImageView.getLocationOnScreen(location)
            val startX = e1.rawX.toInt()
            val startY = e1.rawY.toInt()
            val startXRel = startX - location[0]
            val swipeEdgeLimit = (cameraImageView.width * 0.9).toInt()

            val isInside = startXRel in 0 until swipeEdgeLimit &&
                           startY >= location[1] && startY <= location[1] + cameraImageView.height

            if (!isInside) return false

            val currentTime = System.currentTimeMillis()
            Log.v(TAG, "filtered: currentTime = $currentTime, lastActionTime = $lastActionTime")
            if (currentTime - lastActionTime < MIN_ACTION_INTERVAL) return false

            val diffX = e2.x - e1.x
            val diffY = e2.y - e1.y
            if (abs(diffX) > SWIPE_THRESHOLD) {
                if (diffX > 0) cameraController.loadPreviousImage() else cameraController.loadNextImage()
                lastActionTime = currentTime
            } else if (abs(diffY) > SWIPE_THRESHOLD) {
                cameraController.loadNewImageFromCurrentCamera()
                lastActionTime = currentTime
            }
            return false
        }
    }
}
