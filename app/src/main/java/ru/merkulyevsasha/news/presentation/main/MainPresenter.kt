package ru.merkulyevsasha.news.presentation.main

import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import ru.merkulyevsasha.news.R
import ru.merkulyevsasha.news.domain.NewsInteractor
import ru.merkulyevsasha.news.models.Article
import ru.merkulyevsasha.news.presentation.BasePresenter
import javax.inject.Inject

class MainPresenter @Inject constructor(private val news: NewsInteractor) : BasePresenter<MainView>() {

    private val cached = mutableListOf<Article>()

    private var isFinished = false

    fun onResume(isRefreshing: Boolean, navId: Int, searchText: String?) {
        if (isRefreshing && !isFinished) return
        isFinished = false

        var isSearch = false
        val articles: Single<List<Article>>
        if (searchText == null || searchText.isEmpty()) {
            articles = getArticlesByNavId(navId)
        } else {
            isSearch = true
            articles = news.search(searchText)
        }
        procceed(articles, isSearch)
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
            isFinished = true
            view?.hideProgress()
        }
    }

    fun onRefresh(navId: Int) {
        procceed(news.readNewsAndSaveToDb(navId), false)
    }

    fun onSearch(searchText: String) {
        procceed(news.search(searchText), true)
    }

    fun onCancelSearch(navId: Int) {
        procceed(getArticlesByNavId(navId), false)
    }

    fun onSelectSource(navId: Int) {
        procceed(getArticlesByNavId(navId), false)
    }

    fun onCreateView() {
        view?.showItems(cached)
        compositeDisposable.add(
                news
                        .getFirstRun()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { first ->
                            if (first) {
                                view?.scheduleJob()
                                news.setFirstRunFlag()
                            }
                        }
        )
    }

    private fun getArticlesByNavId(navId: Int): Single<List<Article>> {
        val articles: Single<List<Article>>
        articles = if (navId == R.id.nav_all)
            news.selectAll()
                    .flattenAsFlowable { t -> t }
                    .switchIfEmpty(Flowable.defer { news.readNewsAndSaveToDb(navId).flattenAsFlowable { t -> t } })
                    .toList()
        else
            news.selectNavId(navId)
        return articles
    }

    private fun procceed(articles: Single<List<Article>>, isSearch: Boolean) {
        compositeDisposable.add(
                articles
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe { _ -> view?.showProgress() }
                        .doFinally {
                            isFinished = true
                            view?.hideProgress()
                        }
                        .subscribe({ items ->
                            if (!isSearch) {
                                cached.clear()
                                cached.addAll(items)
                            }
                            if (isSearch && items.isEmpty()) {
                                view?.showNoSearchResultMessage()
                            } else {
                                view?.showItems(items)
                            }
                        },
                                { _ ->
                                    view?.showMessageError()
                                }))
    }
}
