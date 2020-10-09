package ru.merkulyevsasha.sourcearticles.presentation

import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import ru.merkulyevsasha.core.ArticleDistributor
import ru.merkulyevsasha.core.ResourceProvider
import ru.merkulyevsasha.core.domain.ArticlesInteractor
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.core.routers.MainActivityRouter
import ru.merkulyevsasha.coreandroid.base.BaseViewModel
import ru.merkulyevsasha.coreandroid.common.newsadapter.ArticleClickCallbackHandler
import ru.merkulyevsasha.coreandroid.common.newsadapter.ArticleCommentArticleCallbackClickHandler
import ru.merkulyevsasha.coreandroid.common.newsadapter.ArticleLikeCallbackClickHandler
import ru.merkulyevsasha.coreandroid.common.newsadapter.ArticleShareCallbackClickHandler
import ru.merkulyevsasha.coreandroid.presentation.ArticleLikeClickHandler
import ru.merkulyevsasha.sourcearticles.R
import javax.inject.Inject

class SourceArticlesViewModel @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val articlesInteractor: ArticlesInteractor,
    private val newsDistributor: ArticleDistributor,
    private val applicationRouter: MainActivityRouter
) : BaseViewModel(),
    ArticleClickCallbackHandler, ArticleLikeCallbackClickHandler, ArticleShareCallbackClickHandler, ArticleCommentArticleCallbackClickHandler {

    val items = MutableLiveData<List<Article>>()
    val item = MutableLiveData<Article>()

    private val articleLikeClickHandler = ArticleLikeClickHandler(articlesInteractor,
        { item.postValue(it) },
        { messages.postValue(resourceProvider.getString(R.string.articles_activities_loading_error_message)) })

    fun onFirstLoad(sourceName: String) {
        compositeDisposable.add(
            articlesInteractor.getSourceArticles(sourceName)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { progress.postValue(true) }
                .doAfterTerminate { progress.postValue(false) }
                .subscribe(
                    { items.postValue(it) },
                    {
                        messages.postValue(resourceProvider.getString(R.string.articles_activities_loading_error_message))
                    }))
    }

    fun onRefresh(sourceName: String) {
        onFirstLoad(sourceName)
    }

    fun onSearch(sourceId: String, searchText: String?) {
        compositeDisposable.add(articlesInteractor.searchSourceArticles(sourceId, searchText)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { progress.postValue(true) }
            .doAfterTerminate { progress.postValue(false) }
            .subscribe(
                { items.postValue(it) },
                {
                    messages.postValue(resourceProvider.getString(R.string.articles_activities_loading_error_message))
                }))
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

    override fun onArticleCommentArticleClicked(articleId: Int) {
        applicationRouter.showArticleComments(articleId)
    }

    override fun onArticleShareClicked(item: Article) {
        newsDistributor.distribute(item)
    }

}