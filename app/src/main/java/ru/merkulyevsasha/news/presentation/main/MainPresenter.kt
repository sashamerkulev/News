package ru.merkulyevsasha.news.presentation.main

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import ru.merkulyevsasha.news.R
import ru.merkulyevsasha.news.domain.AlreadyRefreshException
import ru.merkulyevsasha.news.domain.NewsInteractor
import ru.merkulyevsasha.news.models.Article
import ru.merkulyevsasha.news.presentation.BasePresenter
import javax.inject.Inject

class MainPresenter @Inject constructor(private val newsInteractor: NewsInteractor) : BasePresenter<MainView>() {

    fun onResume(navId: Int, searchText: String?) {
        if (searchText == null || searchText.isEmpty()) {
            procceed(getArticlesByNavId(navId), false)
        } else {
            procceed(newsInteractor.search(searchText), true)
        }
    }

    fun onPrepareToSearch() {
        view?.prepareToSearch()
    }

    fun onItemClicked(item: Article) {
        view?.showDetailScreen(item)
    }

    fun onReceived(navId: Int, updated: Boolean, finished: Boolean) {
        if (updated) {
            compositeDisposable.add(
                getArticlesByNavId(navId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { items ->
                        view?.showItems(items)
                    })
        }
        if (finished) {
            view?.hideProgress()
        }
    }

    fun onRefresh(navId: Int) {
        procceed(newsInteractor.refreshArticles(navId), false)
    }

    fun onSearch(searchText: String) {
        procceed(newsInteractor.search(searchText), true)
    }

    fun onCancelSearch(navId: Int) {
        procceed(getArticlesByNavId(navId), false)
    }

    fun onSelectSource(navId: Int) {
        procceed(getArticlesByNavId(navId), false)
    }

    fun onCreateView() {
        compositeDisposable.add(
            newsInteractor
                .getFirstRun()
                .doFinally { newsInteractor.setFirstRunFlag() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { first ->
                    if (first) {
                        view?.scheduleJob()
                    }
                }
        )
    }

    private fun getArticlesByNavId(navId: Int): Single<List<Article>> {
        return if (navId == R.id.nav_all) newsInteractor.readOrGetArticles(navId)
        else newsInteractor.readArticlesByNavId(navId)
    }

    private fun procceed(articles: Single<List<Article>>, isSearch: Boolean) {
        compositeDisposable.add(
            articles
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { _ -> view?.showProgress() }
                .doFinally {
                    view?.hideProgress()
                }
                .subscribe({ items ->
                    if (isSearch && items.isEmpty()) {
                        view?.showNoSearchResultMessage()
                    } else {
                        view?.showItems(items)
                    }
                },
                    { throwable ->
                        if (throwable is AlreadyRefreshException)
                        else view?.showMessageError()
                    }))
    }
}
