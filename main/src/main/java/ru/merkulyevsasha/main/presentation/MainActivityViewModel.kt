package ru.merkulyevsasha.main.presentation

import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import ru.merkulyevsasha.core.Logger
import ru.merkulyevsasha.core.domain.SetupInteractor
import ru.merkulyevsasha.core.models.ThemeEnum
import ru.merkulyevsasha.core.preferences.KeyValueStorage
import ru.merkulyevsasha.core.routers.MainActivityRouter
import ru.merkulyevsasha.coreandroid.base.BaseViewModel
import timber.log.Timber
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(
    private val setupInteractor: SetupInteractor,
    private val keyValueStorage: KeyValueStorage,
    private val mainActivityRouter: MainActivityRouter,
    private val l: Logger
) : BaseViewModel() {
    companion object {
        private const val TAG = "MainActivityViewModel"
    }

    val themes = MutableLiveData<ThemeEnum>()

    fun onSetup() {
        compositeDisposable.add(
            setupInteractor.registerSetup()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        themes.postValue(keyValueStorage.getUserProfileTheme())
                        mainActivityRouter.showMainFragment()
                    },
                    {
                        l.e(TAG, it)
                        messages.postValue(it.toString())
                    }))
    }

    fun onUpdateFirebaseId(firebaseId: String) {
        compositeDisposable.add(
            setupInteractor.updateFirebaseToken(firebaseId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { },
                    {
                        Timber.e(it)
                    }))
    }
}