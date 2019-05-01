package ru.merkulyevsasha.news.presentation.splash

import ru.merkulyevsasha.news.presentation.base.BaseView

interface SplashView: BaseView {
    fun showMainScreen()
    fun showFatalError()
}
