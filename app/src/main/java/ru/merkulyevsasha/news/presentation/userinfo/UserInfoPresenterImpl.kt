package ru.merkulyevsasha.news.presentation.userinfo

import io.reactivex.android.schedulers.AndroidSchedulers
import ru.merkulyevsasha.core.domain.UsersInteractor
import ru.merkulyevsasha.news.presentation.base.BasePresenterImpl
import timber.log.Timber

class UserInfoPresenterImpl(private val usersInteractor: UsersInteractor) : BasePresenterImpl<UserInfoView>() {
    fun onFirstLoad() {
        compositeDisposable.add(
            usersInteractor.getUserInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { view?.showProgress() }
                .doAfterTerminate { view?.hideProgress() }
                .subscribe({
                    view?.showUserInfo(it)
                },
                    {
                        Timber.e(it)
                        view?.showError()
                    })

        )
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
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view?.showUserInfo(it)
                },
                    {
                        Timber.e(it)
                        view?.showError()
                    })

        )
    }

    fun onSaveButtonClicked(userName: String) {
        compositeDisposable.add(
            usersInteractor.updateUser(userName, "")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    //view?.showUserInfo(it)
                },
                    {
                        Timber.e(it)
                        view?.showError()
                    })

        )
    }
}