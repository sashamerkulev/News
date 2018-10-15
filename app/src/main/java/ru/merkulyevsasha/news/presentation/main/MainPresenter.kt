package ru.merkulyevsasha.news.presentation.main

import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import ru.merkulyevsasha.news.R
import ru.merkulyevsasha.news.domain.NewsInteractor
import ru.merkulyevsasha.news.models.Article
import ru.merkulyevsasha.news.presentation.BasePresenter
import javax.inject.Inject

class MainPresenter @Inject constructor(private val newsInteractor: NewsInteractor) : BasePresenter<MainView>() {

    fun onResume(navId: Int, searchText: String?) {
        if (searchText == null || searchText.isEmpty()) {
            proceed(getArticlesByNavId(navId, searchText), false)
        } else {
            proceed(newsInteractor.search(searchText), true)
        }
    }

    fun onPrepareToSearch() {
        view?.prepareToSearch()
    }

    fun onItemClicked(item: Article) {
        view?.showDetailScreen(item)
    }

    fun onRefresh(navId: Int, searchText: String?) {
        proceed(
            newsInteractor.refreshArticles(navId, searchText)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { _ -> view?.showProgress() }
                .doFinally {
                    view?.hideProgress()
                }
            , false)
    }

    fun onSearch(searchText: String) {
        proceed(newsInteractor.search(searchText), true)
    }

    fun onCancelSearch(navId: Int) {
        proceed(getArticlesByNavId(navId, null), false)
    }

    fun onSelectSource(navId: Int, searchText: String?) {
        proceed(getArticlesByNavId(navId, searchText), false)
    }

    private fun getArticlesByNavId(navId: Int, searchText: String?): Single<List<Article>> {
        // TODO sorry for that: it is necessary for understanding when progress will be show
        val readOrGet = newsInteractor.readAllArticles()
            .flattenAsFlowable { t -> t }
            .switchIfEmpty(Flowable.defer {
                newsInteractor.refreshArticles(navId, searchText).flattenAsFlowable { t -> t }
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { _ -> view?.showProgress() }
                    .doFinally {
                        view?.hideProgress()
                    }
            })
            .toList()
        return if (navId == R.id.nav_all) readOrGet
        else newsInteractor.readArticlesByNavId(navId)
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
                    { _ ->
                        view?.showMessageError()
                    }))
    }
}
