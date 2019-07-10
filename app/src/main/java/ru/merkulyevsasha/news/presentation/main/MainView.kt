package ru.merkulyevsasha.news.presentation.main

import ru.merkulyevsasha.core.base.BaseView

interface MainView : BaseView {
    fun showMainScreen()
    fun showFatalError()
}