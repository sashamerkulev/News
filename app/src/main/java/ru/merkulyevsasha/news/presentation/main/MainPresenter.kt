package ru.merkulyevsasha.news.presentation.main

import io.reactivex.android.schedulers.AndroidSchedulers
import ru.merkulyevsasha.core.domain.SetupInteractor
import ru.merkulyevsasha.news.presentation.base.BasePresenterImpl
import timber.log.Timber

class MainPresenter(private val setupInteractor: SetupInteractor) : BasePresenterImpl<MainView>() {
    fun onSetup(getFirebaseId: () -> String) {
        compositeDisposable.add(
            setupInteractor.registerSetup(getFirebaseId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { view?.showMainScreen() },
                    {
                        Timber.e(it)
                        view?.showFatalError()
                    }))
    }
}
