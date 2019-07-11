package ru.merkulyevsasha.news.presentation.main

import ru.merkulyevsasha.coreandroid.base.BaseView

interface MainView : ru.merkulyevsasha.coreandroid.base.BaseView {
    fun showMainScreen()
    fun showFatalError()
}