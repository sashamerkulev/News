package ru.merkulyevsasha.userinfo.presentation

import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import ru.merkulyevsasha.core.ResourceProvider
import ru.merkulyevsasha.core.domain.SourceInteractor
import ru.merkulyevsasha.core.domain.UsersInteractor
import ru.merkulyevsasha.core.models.ThemeEnum
import ru.merkulyevsasha.core.models.UserInfo
import ru.merkulyevsasha.core.models.UserProfile
import ru.merkulyevsasha.coreandroid.base.BaseViewModel
import ru.merkulyevsasha.userinfo.R
import javax.inject.Inject

class UserInfoViewModel @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val usersInteractor: UsersInteractor,
    private val sourceInteractor: SourceInteractor
) : BaseViewModel() {

    val userProfile = MutableLiveData<UserProfile>()
    val userInfo = MutableLiveData<UserInfo>()

    fun onFirstLoad() {
        compositeDisposable.add(
            usersInteractor.getUserInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { progress.postValue(true) }
                .doAfterTerminate { progress.postValue(false) }
                .subscribe({
                    userProfile.postValue(it)
                },
                    {
                        messages.postValue(resourceProvider.getString(R.string.user_info_loading_error_message))
                    })

        )
    }

    fun onChangedAvatar(profileFileName: String) {
        compositeDisposable.add(
            usersInteractor.uploadUserPhoto(profileFileName)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { progress.postValue(true) }
                .doAfterTerminate { progress.postValue(false) }
                .subscribe({
                    userInfo.postValue(it)
                },
                    {
                        messages.postValue(resourceProvider.getString(R.string.user_info_loading_error_message))
                    })

        )
    }

    fun onSaveButtonClicked(userName: String) {
        if (userName.isEmpty()) {
            messages.postValue(resourceProvider.getString(R.string.user_info_name_require_error_message))
            return
        }
        compositeDisposable.add(
            usersInteractor.updateUser(userName, "")
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { progress.postValue(true) }
                .doAfterTerminate { progress.postValue(false) }
                .subscribe({
                    messages.postValue(resourceProvider.getString(R.string.user_info_name_save_success_message))
                },
                    {
                        messages.postValue(resourceProvider.getString(R.string.user_info_uniq_name_error_message))
                    })

        )
    }

    fun onThemeChanged(newTheme: ThemeEnum) {
        compositeDisposable.add(
            usersInteractor.updateTheme(newTheme)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { progress.postValue(true) }
                .doAfterTerminate { progress.postValue(false) }
                .subscribe({
                },
                    {
                        messages.postValue(resourceProvider.getString(R.string.user_info_uniq_name_error_message))
                    })

        )
    }

    fun onSourceChecked(checked: Boolean, sourceId: String) {
        compositeDisposable.add(
            sourceInteractor.updateRssSource(checked, sourceId)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { progress.postValue(true) }
                .doAfterTerminate { progress.postValue(false) }
                .subscribe({
                },
                    {
                        messages.postValue(resourceProvider.getString(R.string.user_info_uniq_name_error_message))
                    })

        )
    }
}