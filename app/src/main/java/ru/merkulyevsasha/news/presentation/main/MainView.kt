package ru.merkulyevsasha.news.presentation.main

import ru.merkulyevsasha.coreandroid.base.BaseView

interface MainView : BaseView {
    fun showMainScreen()
    fun showFatalError()
}