package ru.merkulyevsasha.news.presentation.useractivities

import io.reactivex.android.schedulers.AndroidSchedulers
import ru.merkulyevsasha.core.domain.ArticlesInteractor
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.core.routers.MainActivityRouter
import ru.merkulyevsasha.news.presentation.base.BasePresenterImpl
import ru.merkulyevsasha.news.presentation.common.ArticleLikeClickHandler
import ru.merkulyevsasha.news.presentation.common.newsadapter.ArticleClickCallbackHandler
import ru.merkulyevsasha.news.presentation.common.newsadapter.ArticleLikeCallbackClickHandler
import ru.merkulyevsasha.news.presentation.common.newsadapter.ArticleShareCallbackClickHandler
import ru.merkulyevsasha.news.presentation.common.newsadapter.CommentArticleCallbackClickHandler
import timber.log.Timber

class UserActivitiesPresenterImpl(
    private val articlesInteractor: ArticlesInteractor,
    private val applicationRouter: MainActivityRouter
) : BasePresenterImpl<UserActivitiesView>(),
    ArticleClickCallbackHandler, ArticleLikeCallbackClickHandler, ArticleShareCallbackClickHandler, CommentArticleCallbackClickHandler {

    private val articleLikeClickHandler = ArticleLikeClickHandler(articlesInteractor,
        { view?.updateItem(it) },
        { view?.showError() })

    fun onFirstLoad() {
        compositeDisposable.add(
            articlesInteractor.getUserActivityArticles()
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
        onFirstLoad()
    }

    override fun onArticleCliked(item: Article) {
        applicationRouter.showArticleDetails(item.articleId)
    }

    override fun onArticleLikeClicked(item: Article) {
        compositeDisposable.add(articleLikeClickHandler.onArticleLikeClicked(item.articleId))
    }

    override fun onArticleDislikeClicked(item: Article) {
        compositeDisposable.add(articleLikeClickHandler.onArticleDislikeClicked(item.articleId))
    }

    override fun onCommentArticleClicked(articleId: Int) {
        applicationRouter.showArticleComments(articleId)
    }

    override fun onArticleShareClicked(item: Article) {
    }

}