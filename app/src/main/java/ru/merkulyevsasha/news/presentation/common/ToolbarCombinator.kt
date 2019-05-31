package ru.merkulyevsasha.news.presentation.common

import android.support.v7.widget.Toolbar

interface ToolbarCombinator {
    fun bindToolbar(toolbar: Toolbar)
    fun unbindToolbar()
}