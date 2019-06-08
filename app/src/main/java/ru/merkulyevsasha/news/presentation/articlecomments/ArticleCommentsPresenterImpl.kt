package ru.merkulyevsasha.news.presentation.articlecomments

import io.reactivex.android.schedulers.AndroidSchedulers
import ru.merkulyevsasha.core.domain.ArticleCommentsInteractor
import ru.merkulyevsasha.core.domain.ArticlesInteractor
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.core.models.ArticleComment
import ru.merkulyevsasha.core.models.ArticleOrComment
import ru.merkulyevsasha.core.preferences.KeyValueStorage
import ru.merkulyevsasha.news.BuildConfig
import ru.merkulyevsasha.news.presentation.base.BasePresenterImpl
import ru.merkulyevsasha.news.presentation.common.ArticleLikeClickHandler
import ru.merkulyevsasha.news.presentation.common.newsadapter.ArticleLikeCallbackClickHandler
import ru.merkulyevsasha.news.presentation.common.newsadapter.ArticleShareCallbackClickHandler
import ru.merkulyevsasha.news.presentation.common.newsadapter.CommentLikeCallbackClickHandler
import ru.merkulyevsasha.news.presentation.common.newsadapter.CommentShareCallbackClickHandler
import timber.log.Timber
import java.util.*

class ArticleCommentsPresenterImpl(
    private val articleCommentsInteractor: ArticleCommentsInteractor,
    private val articlesInteractor: ArticlesInteractor,
    private val keyValueStorage: KeyValueStorage
) : BasePresenterImpl<ArticleCommentsView>(),
    ArticleLikeCallbackClickHandler, ArticleShareCallbackClickHandler, CommentLikeCallbackClickHandler, CommentShareCallbackClickHandler {

    private val articleLikeClickHandler = ArticleLikeClickHandler(articlesInteractor,
        { view?.updateItem(it) },
        { view?.showError() })

    private val random = Random()

    fun onFirstLoad(articleId: Int) {
        compositeDisposable.add(
            articleCommentsInteractor.getArticleComments(articleId)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { view?.showProgress() }
                .doAfterTerminate { view?.hideProgress() }
                .subscribe({
//                    val testResult = listOf<ArticleComment>(
//                        testArticleComment(it.first, 1),
//                        testArticleComment(it.first, 2),
//                        testArticleComment(it.first, 3, true)
//                    )
//                    val result = listOf<ArticleOrComment>(it.first) + testResult // it.second
                    val result = listOf<ArticleOrComment>(it.first) + it.second
                    view?.showComments(result)
                }, {
                    Timber.e(it)
                    view?.showError()
                }))
    }

    fun onRefresh(articleId: Int) {
        compositeDisposable.add(
            articleCommentsInteractor.refreshAndGetArticleComments(articleId)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { view?.showProgress() }
                .doAfterTerminate { view?.hideProgress() }
                .subscribe({
                    val aaa = listOf<ArticleOrComment>(it.first) + it.second
                    view?.showComments(aaa)
                }, {
                    Timber.e(it)
                    view?.showError()
                }))
    }

    fun onAddCommentClicked(articleId: Int, comment: String) {
        if (comment.isEmpty()) {
            view?.showError()
            return
        }
        compositeDisposable.add(
            articleCommentsInteractor.addArticleComment(articleId, comment)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { view?.showProgress() }
                .doAfterTerminate { view?.hideProgress() }
                .subscribe({
                    view?.updateCommentItem(it)
                }, {
                    Timber.e(it)
                    view?.showError()
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

    override fun onCommentLikeClicked(item: ArticleComment) {
        compositeDisposable.add(
            articleCommentsInteractor.likeArticleComment(item.commentId)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { view?.showProgress() }
                .doAfterTerminate { view?.hideProgress() }
                .subscribe({
                    view?.updateCommentItem(it)
                }, {
                    Timber.e(it)
                    view?.showError()
                }))
    }

    override fun onCommentDislikeClicked(item: ArticleComment) {
        compositeDisposable.add(
            articleCommentsInteractor.dislikeArticleComment(item.commentId)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { view?.showProgress() }
                .doAfterTerminate { view?.hideProgress() }
                .subscribe({
                    view?.updateCommentItem(it)
                }, {
                    Timber.e(it)
                    view?.showError()
                }))
    }

    override fun onCommentShareClicked(item: ArticleComment) {
    }

    private fun testArticleComment(first: Article, userId: Int, owner: Boolean = false): ArticleComment {
        return ArticleComment(
            first.articleId,
            userId,
            userId,
            "ddd",
            Date(),
            "sss",
            0,
            random.nextInt(100),
            random.nextInt(100),
            false,
            true,
            owner,
            BuildConfig.API_URL + "/users/$userId/downloadPhoto",
            keyValueStorage.getAccessToken()
        )
    }

}