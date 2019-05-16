package ru.merkulyevsasha.news.presentation.userinfo

import ru.merkulyevsasha.news.presentation.base.BaseView

interface UserInfoView : BaseView {
    fun showError()
    fun hideProgress()
    fun showProgress()
}
