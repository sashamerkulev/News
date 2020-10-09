package ru.merkulyevsasha.articlecomments.presentation

import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import ru.merkulyevsasha.articlecomments.R
import ru.merkulyevsasha.core.ArticleDistributor
import ru.merkulyevsasha.core.ResourceProvider
import ru.merkulyevsasha.core.domain.ArticleCommentsInteractor
import ru.merkulyevsasha.core.domain.ArticlesInteractor
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.core.models.ArticleComment
import ru.merkulyevsasha.core.models.ArticleOrComment
import ru.merkulyevsasha.coreandroid.base.BaseViewModel
import ru.merkulyevsasha.coreandroid.common.newsadapter.ArticleCommentLikeCallbackClickHandler
import ru.merkulyevsasha.coreandroid.common.newsadapter.ArticleCommentShareCallbackClickHandler
import ru.merkulyevsasha.coreandroid.common.newsadapter.ArticleLikeCallbackClickHandler
import ru.merkulyevsasha.coreandroid.common.newsadapter.ArticleShareCallbackClickHandler
import ru.merkulyevsasha.coreandroid.presentation.ArticleLikeClickHandler
import javax.inject.Inject

class ArticleCommentsViewModel @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val articleCommentsInteractor: ArticleCommentsInteractor,
    articlesInteractor: ArticlesInteractor,
    private val newsDistributor: ArticleDistributor
) : BaseViewModel(),
    ArticleLikeCallbackClickHandler, ArticleShareCallbackClickHandler,
    ArticleCommentLikeCallbackClickHandler, ArticleCommentShareCallbackClickHandler {

    val article = MutableLiveData<Article>()
    val updateArticleComment = MutableLiveData<ArticleComment>()
    val items = MutableLiveData<List<ArticleOrComment>>()

    private val articleLikeClickHandler = ArticleLikeClickHandler(articlesInteractor,
        { article.postValue(it) },
        { messages.postValue(resourceProvider.getString(R.string.comment_loading_error_message)) })

    fun onFirstLoad(articleId: Int) {
        compositeDisposable.add(
            articleCommentsInteractor.getArticleComments(articleId)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { progress.postValue(true) }
                .doAfterTerminate { progress.postValue(false) }
                .subscribe({
                    val result = listOf<ArticleOrComment>(it.first) + it.second
                    items.postValue(result)
                }, {
                    messages.postValue(resourceProvider.getString(R.string.comment_loading_error_message))
                }))
    }

    fun onRefresh(articleId: Int) {
        compositeDisposable.add(
            articleCommentsInteractor.refreshAndGetArticleComments(articleId)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { progress.postValue(true) }
                .doAfterTerminate { progress.postValue(false) }
                .subscribe({
                    val result = listOf<ArticleOrComment>(it.first) + it.second
                    items.postValue(result)
                }, {
                    messages.postValue(resourceProvider.getString(R.string.comment_loading_error_message))
                }))
    }

    fun onAddCommentClicked(articleId: Int, comment: String) {
        if (comment.isEmpty()) {
            messages.postValue(resourceProvider.getString(R.string.comment_loading_error_message))
            return
        }
        compositeDisposable.add(
            articleCommentsInteractor.addArticleComment(articleId, comment)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { progress.postValue(true) }
                .doAfterTerminate { progress.postValue(false) }
                .subscribe({
                    updateArticleComment.postValue(it)
                }, {
                    messages.postValue(resourceProvider.getString(R.string.comment_loading_error_message))
                }))
    }

    override fun onArticleLikeClicked(item: Article) {
        compositeDisposable.add(articleLikeClickHandler.onArticleLikeClicked(item.articleId))
    }

    override fun onArticleDislikeClicked(item: Article) {
        compositeDisposable.add(articleLikeClickHandler.onArticleDislikeClicked(item.articleId))
    }

    override fun onArticleShareClicked(item: Article) {
    }

    override fun onArticleCommentLikeClicked(item: ArticleComment) {
        compositeDisposable.add(
            articleCommentsInteractor.likeArticleComment(item.commentId)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { progress.postValue(true) }
                .doAfterTerminate { progress.postValue(false) }
                .subscribe({
                    updateArticleComment.postValue(it)
                }, {
                    messages.postValue(resourceProvider.getString(R.string.comment_loading_error_message))
                }))
    }

    override fun onArticleCommentDislikeClicked(item: ArticleComment) {
        compositeDisposable.add(
            articleCommentsInteractor.dislikeArticleComment(item.commentId)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { progress.postValue(true) }
                .doAfterTerminate { progress.postValue(false) }
                .subscribe({
                    updateArticleComment.postValue(it)
                }, {
                    messages.postValue(resourceProvider.getString(R.string.comment_loading_error_message))
                }))
    }

    override fun onArticleCommentShareClicked(item: ArticleComment) {
        newsDistributor.distribute(item)
    }

}