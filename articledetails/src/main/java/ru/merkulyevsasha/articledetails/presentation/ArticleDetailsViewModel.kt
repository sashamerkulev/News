package ru.merkulyevsasha.articledetails.presentation

import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import ru.merkulyevsasha.articledetails.R
import ru.merkulyevsasha.core.ArticleDistributor
import ru.merkulyevsasha.core.ResourceProvider
import ru.merkulyevsasha.core.domain.ArticlesInteractor
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.core.routers.MainActivityRouter
import ru.merkulyevsasha.coreandroid.base.BaseViewModel
import ru.merkulyevsasha.coreandroid.presentation.ArticleLikeClickHandler
import javax.inject.Inject

class ArticleDetailsViewModel @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val articlesInteractor: ArticlesInteractor,
    private val newsDistributor: ArticleDistributor,
    private val applicationRouter: MainActivityRouter
) : BaseViewModel() {

    val addItem = MutableLiveData<Article>()
    val updateItem = MutableLiveData<Article>()

    private val articleLikeClickHandler = ArticleLikeClickHandler(articlesInteractor,
        { updateItem.postValue(it) },
        { messages.postValue(resourceProvider.getString(R.string.article_details_loading_error_message)) })

    fun onFirstLoad(articleId: Int) {
        compositeDisposable.add(
            articlesInteractor.getArticle(articleId)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { progress.postValue(true) }
                .doAfterTerminate { progress.postValue(false) }
                .subscribe({
                    addItem.postValue(it)
                },
                    {
                        messages.postValue(it.message)
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
                .doOnSubscribe { progress.postValue(true) }
                .doAfterTerminate { progress.postValue(false) }
                .subscribe(
                    {
                        newsDistributor.distribute(it)
                    },
                    {
                        messages.postValue(it.message)
                    }))
    }
}