package ru.merkulyevsasha.news.presentation.articledetails

import io.reactivex.android.schedulers.AndroidSchedulers
import ru.merkulyevsasha.core.domain.ArticlesInteractor
import ru.merkulyevsasha.core.routers.ApplicationRouter
import ru.merkulyevsasha.news.presentation.base.BasePresenterImpl
import timber.log.Timber

class ArticleDetailsPresenterImpl(
    private val articlesInteractor: ArticlesInteractor,
    private val applicationRouter: ApplicationRouter
) : BasePresenterImpl<ArticleDetailsView>() {
    fun onFirstLoad(articleId: Int) {
        compositeDisposable.add(
            articlesInteractor.getArticle(articleId)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { view?.showProgress() }
                .doAfterTerminate { view?.hideProgress() }
                .subscribe(
                    { view?.showItem(it) },
                    {
                        Timber.e(it)
                        view?.showError()
                    }))
    }

    fun onLikeClicked(articleId: Int) {
        compositeDisposable.add(
            articlesInteractor.likeArticle(articleId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { item -> view?.updateItem(item) },
                    {
                        Timber.e(it)
                        view?.showError()
                    }))
    }

    fun onDislikeClicked(articleId: Int) {
        compositeDisposable.add(
            articlesInteractor.dislikeArticle(articleId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { item -> view?.updateItem(item) },
                    {
                        Timber.e(it)
                        view?.showError()
                    }))
    }

    fun onCommentClicked(articleId: Int) {
        applicationRouter.showArticleComments(articleId)
    }

    fun onShareClicked(articleId: Int) {
    }
}