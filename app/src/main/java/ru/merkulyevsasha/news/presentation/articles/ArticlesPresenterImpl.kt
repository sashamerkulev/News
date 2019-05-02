package ru.merkulyevsasha.news.presentation.articles

import io.reactivex.android.schedulers.AndroidSchedulers
import ru.merkulyevsasha.core.domain.ArticlesInteractor
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.news.presentation.base.BasePresenterImpl
import timber.log.Timber

class ArticlesPresenterImpl(private val articlesInteractor: ArticlesInteractor) : BasePresenterImpl<ArticlesView>() {
    fun onFirstLoadArticles() {
        compositeDisposable.add(
            articlesInteractor.getArticles()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { view?.showProgress() }
                .doAfterTerminate { view?.hideProgress() }
                .subscribe(
                    { view?.showItems(it) },
                    {
                        Timber.e(it)
                        view?.showError()
                    }))
    }

    fun onRefresh() {
        compositeDisposable.add(
            articlesInteractor.refreshAndGetArticles()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { view?.showProgress() }
                .doAfterTerminate { view?.hideProgress() }
                .subscribe(
                    { view?.updateItems(it) },
                    {
                        Timber.e(it)
                        view?.showError()
                    }))
    }

    fun onArticleCliked(newItem: Article) {

    }

}