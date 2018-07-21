package ru.merkulyevsasha.news.presentation.main

import android.support.design.widget.AppBarLayout
import android.view.MotionEvent
import android.view.View

/**
 * Created by sasha_merkulev on 01.10.2017.
 */

class AppbarScrollExpander internal constructor(view: View, appbarlayout: AppBarLayout) {

    private val touchPoint: TouchPoint
    var expanded: Boolean = false

    init {
        touchPoint = TouchPoint()
        view.setOnTouchListener { _, event ->
            val action = event.action
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    touchPoint.y = event.y
                    touchPoint.x = event.x
                }
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                    val y = event.y
                    expanded = y - touchPoint.y > 0
                    appbarlayout.setExpanded(expanded)
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
