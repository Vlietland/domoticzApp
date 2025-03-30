package com.wiconic.domoticzapp.ui.controller

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

class SwipeGestureHandler(
    private val cameraController: CameraController
) : View.OnTouchListener {

    private val gestureDetector = GestureDetector(cameraController.context, GestureListener())

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {

        private val SWIPE_THRESHOLD = 100
        private val SWIPE_VELOCITY_THRESHOLD = 100

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            if (e1 == null || e2 == null) return false

            val diffX = e2.x - e1.x
            val diffY = e2.y - e1.y

            return if (abs(diffX) > abs(diffY)) {
                // Left or Right swipe
                if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        onSwipeRight()
                    } else {
                        onSwipeLeft()
                    }
                    true
                } else false
            } else {
                // Up or Down swipe
                if (abs(diffY) > SWIPE_THRESHOLD && abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeDown()
                    } else {
                        onSwipeUp()
                    }
                    true
                } else false
            }
        }

        private fun onSwipeRight() {
            cameraController.loadPreviousCameraImage()
        }

        private fun onSwipeLeft() {
            cameraController.loadNextCameraImage()
        }

        private fun onSwipeUp() {
            cameraController.loadNewImageFromCurrentCamera()
        }

        private fun onSwipeDown() {
            cameraController.loadNewImageFromCurrentCamera()
        }
    }
}
