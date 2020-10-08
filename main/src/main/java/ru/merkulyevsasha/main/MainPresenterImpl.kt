package ru.merkulyevsasha.main

import io.reactivex.android.schedulers.AndroidSchedulers
import ru.merkulyevsasha.core.Logger
import ru.merkulyevsasha.core.domain.SetupInteractor
import ru.merkulyevsasha.core.preferences.KeyValueStorage
import ru.merkulyevsasha.core.routers.MainActivityRouter
import ru.merkulyevsasha.coreandroid.base.BasePresenterImpl
import timber.log.Timber
import javax.inject.Inject

class MainPresenterImpl @Inject constructor(
    private val setupInteractor: SetupInteractor,
    private val keyValueStorage: KeyValueStorage,
    private val mainActivityRouter: MainActivityRouter,
    private val l: Logger
) : BasePresenterImpl<MainView>() {
    companion object {
        private const val TAG = "MainPresenter"
    }

    fun onSetup() {
        compositeDisposable.add(
            setupInteractor.registerSetup()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        addCommand {
                            view?.changeTheme(keyValueStorage.getUserProfileTheme())
                        }
                        mainActivityRouter.showMainFragment()
                    },
                    {
                        l.e(TAG, it)
                        view?.showFatalError(it.toString())
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
