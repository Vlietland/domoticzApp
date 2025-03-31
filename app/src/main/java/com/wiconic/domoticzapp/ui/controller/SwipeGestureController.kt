package com.wiconic.domoticzapp.ui.controller

import android.content.Context
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat
import kotlin.math.abs

class SwipeGestureController(
    private val context: Context,
    private val cameraController: CameraController
) : View.OnTouchListener {

    private val gestureDetector = GestureDetectorCompat(context, GestureListener())

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        val result = gestureDetector.onTouchEvent(event)
        Log.d(TAG, "onTouch event received: ${event.action}, Result: $result")
        return result
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        private val SWIPE_THRESHOLD = 25
        private var lastActionTime: Long = 0
        private val MIN_ACTION_INTERVAL = 500L    

        override fun onDown(e: MotionEvent): Boolean {
            Log.d(TAG, "onDown detected. Event: $e")
            return true
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            if (e1 == null || e2 == null) return false
            val currentTime = System.currentTimeMillis()
            Log.d(TAG, "filtered: currentTime = $currentTime, lastActionTime = $lastActionTime")
            if (currentTime - lastActionTime < MIN_ACTION_INTERVAL) {
                //Log.d(TAG, "filtered: {currentTime} : {lastActionTime}")
                return false
            }            
            val diffX = e2.x - e1.x
            val diffY = e2.y - e1.y
            if (abs(diffX) > SWIPE_THRESHOLD) {
                if (diffX > 0) cameraController.loadPreviousImage() else cameraController.loadNextImage()
                lastActionTime = currentTime            
            } 
            else if (abs(diffY) > SWIPE_THRESHOLD) {
                cameraController.loadNewImageFromCurrentCamera()
                lastActionTime = currentTime                            
            }
            return false
        }
    }

    companion object {
        private const val TAG = "SwipeGestureController"
    }
}
