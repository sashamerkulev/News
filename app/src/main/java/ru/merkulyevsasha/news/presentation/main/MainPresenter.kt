package ru.merkulyevsasha.news.presentation.main

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import ru.merkulyevsasha.news.domain.NewsInteractor
import ru.merkulyevsasha.news.domain.NoNeedRefreshException
import ru.merkulyevsasha.news.models.Article
import ru.merkulyevsasha.news.presentation.BasePresenter
import javax.inject.Inject


class MainPresenter @Inject constructor(
    private val newsInteractor: NewsInteractor
) : BasePresenter<MainView>() {

    fun onResume(navId: Int, searchText: String?) {
        compositeDisposable.add(newsInteractor.getProgress()
            .flatMap { progress ->
                if (progress) view?.showProgress()
                else view?.hideProgress()
                if (searchText == null || searchText.isEmpty()) {
                    newsInteractor.readArticlesByNavId(navId)
                } else {
                    newsInteractor.search(searchText)
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ items ->
                if ((searchText == null || searchText.isEmpty()) && items.isEmpty()) {
                    view?.showProgress()
                    newsInteractor.startRefreshWorker(navId, searchText)
                } else if (items.isEmpty()) {
                    view?.showNoSearchResultMessage()
                } else {
                    view?.showItems(items)
                }
            },
                { throwable ->
                    view?.showMessageError()
                }))
    }

    fun onPrepareToSearch() {
        view?.prepareToSearch()
    }

    fun onItemClicked(item: Article) {
        view?.showDetailScreen(item)
    }

    fun onRefresh(navId: Int, searchText: String?) {
        newsInteractor.startRefreshWorker(navId, searchText)
    }

    fun onRefreshEnd(navId: Int, searchText: String?) {
        proceed(newsInteractor.readArticlesByNavId(navId, searchText), true)
    }

    fun onSearch(searchText: String) {
        proceed(newsInteractor.search(searchText), true)
    }

    fun onCancelSearch(navId: Int) {
        proceed(newsInteractor.readArticlesByNavId(navId), false)
    }

    fun onSelectSource(navId: Int, searchText: String?) {
        proceed(newsInteractor.readArticlesByNavId(navId, searchText), false)
    }

    private fun proceed(articles: Single<List<Article>>, isSearch: Boolean) {
        compositeDisposable.add(
            articles
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ items ->
                    if (isSearch && items.isEmpty()) {
                        view?.showNoSearchResultMessage()
                    } else {
                        view?.showItems(items)
                    }
                },
                    { throwable ->
                        if (throwable is NoNeedRefreshException)
                        else view?.showMessageError()
                    }))
    }

}
