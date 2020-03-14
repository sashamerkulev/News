package ru.merkulyevsasha.articles

import io.reactivex.android.schedulers.AndroidSchedulers
import ru.merkulyevsasha.core.ArticleDistributor
import ru.merkulyevsasha.core.Logger
import ru.merkulyevsasha.core.domain.ArticlesInteractor
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.core.routers.MainActivityRouter
import ru.merkulyevsasha.coreandroid.base.BasePresenterImpl
import ru.merkulyevsasha.coreandroid.common.newsadapter.ArticleClickCallbackHandler
import ru.merkulyevsasha.coreandroid.common.newsadapter.ArticleCommentArticleCallbackClickHandler
import ru.merkulyevsasha.coreandroid.common.newsadapter.ArticleLikeCallbackClickHandler
import ru.merkulyevsasha.coreandroid.common.newsadapter.ArticleShareCallbackClickHandler
import ru.merkulyevsasha.coreandroid.common.newsadapter.SourceArticleClickCallbackHandler
import ru.merkulyevsasha.coreandroid.presentation.ArticleLikeClickHandler
import ru.merkulyevsasha.coreandroid.presentation.SearchArticleHandler
import timber.log.Timber

class ArticlesPresenterImpl(
    private val articlesInteractor: ArticlesInteractor,
    private val newsDistributor: ArticleDistributor,
    private val applicationRouter: MainActivityRouter
) : BasePresenterImpl<ArticlesView>(),
    ArticleClickCallbackHandler, SourceArticleClickCallbackHandler, ArticleLikeCallbackClickHandler,
    ArticleShareCallbackClickHandler, ArticleCommentArticleCallbackClickHandler {

    private val articleLikeClickHandler = ArticleLikeClickHandler(articlesInteractor,
        { addCommand { view?.updateItem(it) } },
        { addCommand { view?.showError() } })

    private val searchArticleHandler = SearchArticleHandler(articlesInteractor, false,
        { addCommand { view?.showProgress() } },
        { addCommand { view?.hideProgress() } },
        { addCommand { view?.showItems(it) } },
        { view?.showError() })

    fun onFirstLoad() {
        compositeDisposable.add(
            articlesInteractor.getArticles()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { addCommand { view?.showProgress() } }
                .doAfterTerminate { addCommand { view?.hideProgress() } }
                .subscribe(
                    { addCommand { view?.showItems(it) } },
                    {
                        Timber.e(it)
                        view?.showError()
                    }))
    }

    fun onRefresh() {
        compositeDisposable.add(
            articlesInteractor.refreshAndGetArticles()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { addCommand { view?.showProgress() } }
                .doAfterTerminate { addCommand { view?.hideProgress() } }
                .subscribe(
                    { addCommand { view?.updateItems(it) } },
                    {
                        Timber.e(it)
                        view?.showError()
                    }))
    }

    fun onSearch(searchText: String?) {
        compositeDisposable.add(searchArticleHandler.onSearchArticles(searchText))
    }

    override fun onArticleCliked(item: Article) {
        Logger.log("onArticleCliked item -> ${item.articleId}")
        applicationRouter.showArticleDetails(item.articleId)
    }

    override fun onArticleLikeClicked(item: Article) {
        compositeDisposable.add(articleLikeClickHandler.onArticleLikeClicked(item.articleId))
    }

    override fun onArticleDislikeClicked(item: Article) {
        compositeDisposable.add(articleLikeClickHandler.onArticleDislikeClicked(item.articleId))
    }

    override fun onArticleCommentArticleClicked(articleId: Int) {
        applicationRouter.showArticleComments(articleId)
    }

    override fun onArticleShareClicked(item: Article) {
        newsDistributor.distribute(item)
    }

    override fun onSourceArticleCliked(sourceId: String, sourceName: String) {
        applicationRouter.showSourceArticles(sourceId, sourceName)
    }

}