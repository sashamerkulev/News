package ru.merkulyevsasha.main

import ru.merkulyevsasha.core.models.ThemeEnum
import ru.merkulyevsasha.coreandroid.base.BaseView

interface MainView : BaseView {
    fun changeTheme(theme: ThemeEnum)
    fun showFatalError(err: String)
}