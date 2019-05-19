package ru.merkulyevsasha.news.presentation.userinfo

import ru.merkulyevsasha.core.domain.UsersInteractor
import ru.merkulyevsasha.news.presentation.base.BasePresenterImpl
import timber.log.Timber

class UserInfoPresenterImpl(private val usersInteractor: UsersInteractor) : BasePresenterImpl<UserInfoView>() {
    fun onFirstLoad() {
    }

    fun onLoadCameraClicked() {
        view?.takeCameraPicture()
    }

    fun onLoadGalleryClick() {
        view?.takeGalleryPicture()
    }

    fun onChangedAvatar(profileFileName: String) {
        compositeDisposable.add(
            usersInteractor.uploadUserPhoto(profileFileName)
                .subscribe({
                },
                    {
                        Timber.e(it)
                        view?.showError()
                    })

        )
    }
}