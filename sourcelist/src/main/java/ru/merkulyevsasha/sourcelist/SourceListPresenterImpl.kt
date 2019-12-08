package ru.merkulyevsasha.sourcelist

import io.reactivex.android.schedulers.AndroidSchedulers
import ru.merkulyevsasha.core.domain.SourceInteractor
import ru.merkulyevsasha.core.routers.MainActivityRouter
import ru.merkulyevsasha.coreandroid.base.BasePresenterImpl
import timber.log.Timber

class SourceListPresenterImpl(
    private val sourceInteractor: SourceInteractor,
    private val mainActivityRouter: MainActivityRouter
) : BasePresenterImpl<SourceListView>() {

    fun onFirstLoad() {
        compositeDisposable.add(
            sourceInteractor.getRssSources()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { view?.showProgress() }
                .doAfterTerminate { view?.hideProgress() }
                .subscribe({
                    addCommand { view?.showItems(it) }
                },
                    {
                        Timber.e(it)
                        view?.showError()
                    })
        )
    }

    fun onRefresh() {
        onFirstLoad()
    }

    fun onSourceClicked(sourceId: String, sourceName: String) {
        mainActivityRouter.showSourceArticles(sourceId, sourceName)
    }

}