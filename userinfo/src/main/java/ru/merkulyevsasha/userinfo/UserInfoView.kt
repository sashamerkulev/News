package ru.merkulyevsasha.userinfo

import ru.merkulyevsasha.core.models.UserInfo
import ru.merkulyevsasha.coreandroid.base.BaseView

interface UserInfoView : BaseView {
    fun showError()
    fun hideProgress()
    fun showProgress()
    fun takeGalleryPicture()
    fun takeCameraPicture()
    fun showUserInfo(userInfo: UserInfo)
    fun showNameRequiredValidationMessage()
    fun showSuccesSaving()
    fun showSaveError()
}
