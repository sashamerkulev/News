package ru.merkulyevsasha.userinfo

import io.reactivex.android.schedulers.AndroidSchedulers
import ru.merkulyevsasha.core.domain.SourceInteractor
import ru.merkulyevsasha.core.domain.UsersInteractor
import ru.merkulyevsasha.core.models.ThemeEnum
import ru.merkulyevsasha.coreandroid.base.BasePresenterImpl
import timber.log.Timber

class UserInfoPresenterImpl(
    private val usersInteractor: UsersInteractor,
    private val sourceInteractor: SourceInteractor
) : BasePresenterImpl<UserInfoView>() {
    fun onFirstLoad() {
        compositeDisposable.add(
            usersInteractor.getUserInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { view?.showProgress() }
                .doAfterTerminate { view?.hideProgress() }
                .subscribe({
                    addCommand { view?.showUserProfile(it) }
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
                    addCommand { view?.showUserInfo(it) }
                },
                    {
                        Timber.e(it)
                        view?.showError()
                    })

        )
    }

    fun onSaveButtonClicked(userName: String) {
        if (userName.isEmpty()) {
            view?.showNameRequiredValidationMessage()
            return
        }
        compositeDisposable.add(
            usersInteractor.updateUser(userName, "")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    addCommand { view?.showSuccesSaving() }
                },
                    {
                        Timber.e(it)
                        view?.showSaveError()
                    })

        )
    }

    fun onThemeChanged(newTheme: ThemeEnum) {
        compositeDisposable.add(
            usersInteractor.updateTheme(newTheme)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                },
                    {
                        Timber.e(it)
                        view?.showSaveError()
                    })

        )
    }

    fun onSourceChecked(checked: Boolean, sourceId: String) {
        compositeDisposable.add(
            sourceInteractor.updateRssSource(checked, sourceId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                },
                    {
                        Timber.e(it)
                        view?.showSaveError()
                    })

        )
    }
}