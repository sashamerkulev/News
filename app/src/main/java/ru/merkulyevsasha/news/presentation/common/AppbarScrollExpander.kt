package ru.merkulyevsasha.news.presentation.common

import android.view.MotionEvent
import android.view.View

class AppbarScrollExpander(
    view: View,
    showActionBarListener: ShowActionBarListener?
) {
    private val touchPoint = TouchPoint()

    init {
        view.setOnTouchListener { _, event ->
            val action = event.action
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    touchPoint.y = event.y
                    touchPoint.x = event.x
                }
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                    val y = event.y
                    val isShow = y - touchPoint.y > 0
                    showActionBarListener?.onShowActionBar(isShow)
                }
            }
            false
        }
    }

    private inner class TouchPoint {
        internal var x: Float = 0f
        internal var y: Float = 0f
    }
}
