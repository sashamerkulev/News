package ru.merkulyevsasha.news.presentation.userinfo

import ru.merkulyevsasha.core.models.UserInfo
import ru.merkulyevsasha.core.base.BaseView

interface UserInfoView : BaseView {
    fun showError()
    fun hideProgress()
    fun showProgress()
    fun takeGalleryPicture()
    fun takeCameraPicture()
    fun showUserInfo(userInfo: UserInfo)
    fun showNameRequiredValidationMessage()
    fun showSuccesSaving()
}
