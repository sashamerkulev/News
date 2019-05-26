package ru.merkulyevsasha.news.presentation.useractivities

import io.reactivex.android.schedulers.AndroidSchedulers
import ru.merkulyevsasha.core.routers.ApplicationRouter
import ru.merkulyevsasha.core.domain.ArticlesInteractor
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.news.presentation.base.BasePresenterImpl
import ru.merkulyevsasha.news.presentation.common.newsadapter.CallbackClickHandler
import timber.log.Timber

class UserActivitiesPresenterImpl(
    private val articlesInteractor: ArticlesInteractor,
    private val applicationRouter: ApplicationRouter
) : BasePresenterImpl<UserActivitiesView>(), CallbackClickHandler {
    fun onFirstLoadArticles() {
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
        onFirstLoadArticles()
    }

    override fun onArticleCliked(item: Article) {
        applicationRouter.showArticleDetails(item.articleId)
    }

    override fun onLikeClicked(item: Article) {
        compositeDisposable.add(
            articlesInteractor.likeArticle(item.articleId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { newItem -> view?.updateItem(newItem) },
                    {
                        Timber.e(it)
                        view?.showError()
                    }))
    }

    override fun onDislikeClicked(item: Article) {
        compositeDisposable.add(
            articlesInteractor.dislikeArticle(item.articleId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { newItem -> view?.updateItem(newItem) },
                    {
                        Timber.e(it)
                        view?.showError()
                    }))
    }

    override fun onCommentClicked(articleId: Int) {
    }

    override fun onShareClicked(item: Article) {
    }

}