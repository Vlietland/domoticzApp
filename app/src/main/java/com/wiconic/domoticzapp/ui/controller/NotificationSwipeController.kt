package com.wiconic.domoticzapp.ui.controller

import android.content.Context
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.GestureDetectorCompat
import kotlin.math.abs

class NotificationSwipeController(
    private val context: Context,
    private val notificationCard: CardView,
    private val messagesTextView: TextView
) : View.OnTouchListener {

    private val gestureDetector = GestureDetectorCompat(context, GestureListener())

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (v == null) return false

        val location = IntArray(2)
        notificationCard.getLocationOnScreen(location)
        val x = event.rawX.toInt()
        val y = event.rawY.toInt()
        val isInside = x >= location[0] && x <= location[0] + notificationCard.width &&
                       y >= location[1] && y <= location[1] + notificationCard.height

        return if (isInside) {
            val result = gestureDetector.onTouchEvent(event)
            Log.d(TAG, "onTouch event received: ${event.action}, Result: $result")
            result
        } else {
            false
        }
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        private val SWIPE_THRESHOLD = 25
        private val SWIPE_VELOCITY_THRESHOLD = 25
        private var lastActionTime: Long = 0
        private val MIN_ACTION_INTERVAL = 500L

        override fun onDown(e: MotionEvent): Boolean {
            Log.d(TAG, "onDown detected. Event: $e")
            return true
        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            if (e1 == null || e2 == null) return false
            
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastActionTime < MIN_ACTION_INTERVAL) {
                Log.d(TAG, "filtered: $currentTime : $lastActionTime")
                return false
            }

            val diffY = e2.y - e1.y
            if (abs(diffY) > SWIPE_THRESHOLD && abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffY < 0) { // Swipe up
                    messagesTextView.text = ""
                    lastActionTime = currentTime
                    Log.d(TAG, "Cleared messages on swipe up")
                    return true
                }
            }
            return false
        }
    }

    companion object {
        private const val TAG = "NotificationSwipeController"
    }
}
