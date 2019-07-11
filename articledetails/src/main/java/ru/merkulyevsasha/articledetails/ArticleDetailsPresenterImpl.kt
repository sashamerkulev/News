package ru.merkulyevsasha.articledetails

import io.reactivex.android.schedulers.AndroidSchedulers
import ru.merkulyevsasha.core.NewsDistributor
import ru.merkulyevsasha.coreandroid.base.BasePresenterImpl
import ru.merkulyevsasha.core.domain.ArticlesInteractor
import ru.merkulyevsasha.coreandroid.presentation.ArticleLikeClickHandler
import ru.merkulyevsasha.core.routers.MainActivityRouter
import timber.log.Timber

class ArticleDetailsPresenterImpl(
    private val articlesInteractor: ArticlesInteractor,
    private val newsDistributor: NewsDistributor,
    private val applicationRouter: MainActivityRouter
) : ru.merkulyevsasha.coreandroid.base.BasePresenterImpl<ArticleDetailsView>() {

    private val articleLikeClickHandler = ru.merkulyevsasha.coreandroid.presentation.ArticleLikeClickHandler(articlesInteractor,
        { view?.updateItem(it) },
        { view?.showError() })

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

    fun onArticleLikeClicked(articleId: Int) {
        compositeDisposable.add(articleLikeClickHandler.onArticleLikeClicked(articleId))
    }

    fun onArticleDislikeClicked(articleId: Int) {
        compositeDisposable.add(articleLikeClickHandler.onArticleDislikeClicked(articleId))
    }

    fun onCommentClicked(articleId: Int) {
        applicationRouter.showArticleComments(articleId)
    }

    fun onShareClicked(articleId: Int) {
        compositeDisposable.add(
            articlesInteractor.getArticle(articleId)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { view?.showProgress() }
                .doAfterTerminate { view?.hideProgress() }
                .subscribe(
                    {
                        newsDistributor.distribute(it)
                    },
                    {
                        Timber.e(it)
                        view?.showError()
                    }))
    }
}