package ru.merkulyevsasha.coreandroid.common

import androidx.appcompat.widget.Toolbar

interface ToolbarCombinator {
    fun bindToolbar(toolbar: Toolbar)
    fun unbindToolbar()
}