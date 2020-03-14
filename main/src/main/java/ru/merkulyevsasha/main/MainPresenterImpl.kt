package ru.merkulyevsasha.main

import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import ru.merkulyevsasha.core.Logger
import ru.merkulyevsasha.core.domain.SetupInteractor
import ru.merkulyevsasha.coreandroid.base.BasePresenterImpl
import timber.log.Timber

class MainPresenterImpl(private val setupInteractor: SetupInteractor) : BasePresenterImpl<MainView>() {
    fun onSetup() {
        compositeDisposable.add(
            setupInteractor.registerSetup()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        addCommand { view?.showMainScreen() }
                    },
                    {
                        Logger.log(it.toString())
                        Logger.logStacktrace(it.stackTrace.toList())
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
