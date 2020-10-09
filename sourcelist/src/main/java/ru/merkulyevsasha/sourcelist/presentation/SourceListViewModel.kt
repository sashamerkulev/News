package ru.merkulyevsasha.sourcelist.presentation

import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import ru.merkulyevsasha.core.domain.SourceInteractor
import ru.merkulyevsasha.core.models.RssSource
import ru.merkulyevsasha.core.routers.MainActivityRouter
import ru.merkulyevsasha.coreandroid.base.BaseViewModel
import javax.inject.Inject

class SourceListViewModel @Inject constructor(
    private val sourceInteractor: SourceInteractor,
    private val mainActivityRouter: MainActivityRouter
) : BaseViewModel() {

    val items = MutableLiveData<List<RssSource>>()

    fun onFirstLoad() {
        compositeDisposable.add(
            sourceInteractor.getRssSources()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { progress.postValue(true) }
                .doAfterTerminate { progress.postValue(false) }
                .subscribe({
                    items.postValue(it)
                },
                    {
                        messages.postValue(it.message)
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