package ru.merkulyevsasha.news.presentation.common

import androidx.appcompat.widget.Toolbar

interface ToolbarCombinator {
    fun bindToolbar(toolbar: Toolbar)
    fun unbindToolbar()
}