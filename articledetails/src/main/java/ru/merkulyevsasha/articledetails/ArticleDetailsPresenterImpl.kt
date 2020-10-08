package ru.merkulyevsasha.articledetails

import io.reactivex.android.schedulers.AndroidSchedulers
import ru.merkulyevsasha.core.ArticleDistributor
import ru.merkulyevsasha.core.domain.ArticlesInteractor
import ru.merkulyevsasha.core.routers.MainActivityRouter
import ru.merkulyevsasha.coreandroid.base.BasePresenterImpl
import ru.merkulyevsasha.coreandroid.presentation.ArticleLikeClickHandler
import timber.log.Timber
import javax.inject.Inject

class ArticleDetailsPresenterImpl @Inject constructor(
    private val articlesInteractor: ArticlesInteractor,
    private val newsDistributor: ArticleDistributor,
    private val applicationRouter: MainActivityRouter
) : BasePresenterImpl<ArticleDetailsView>() {

    private val articleLikeClickHandler = ArticleLikeClickHandler(articlesInteractor,
        { addCommand { view?.updateItem(it) } },
        { view?.showError() })

    fun onFirstLoad(articleId: Int) {
        compositeDisposable.add(
            articlesInteractor.getArticle(articleId)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { view?.showProgress() }
                .doAfterTerminate { view?.hideProgress() }
                .subscribe(
                    { addCommand { view?.showItem(it) } },
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
                        addCommand { newsDistributor.distribute(it) }
                    },
                    {
                        Timber.e(it)
                        view?.showError()
                    }))
    }
}