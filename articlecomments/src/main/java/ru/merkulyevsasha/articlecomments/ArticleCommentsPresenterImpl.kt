package ru.merkulyevsasha.articlecomments

import io.reactivex.android.schedulers.AndroidSchedulers
import ru.merkulyevsasha.core.NewsDistributor
import ru.merkulyevsasha.core.domain.ArticleCommentsInteractor
import ru.merkulyevsasha.core.domain.ArticlesInteractor
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.core.models.ArticleComment
import ru.merkulyevsasha.core.models.ArticleOrComment
import ru.merkulyevsasha.coreandroid.base.BasePresenterImpl
import ru.merkulyevsasha.coreandroid.common.newsadapter.ArticleLikeCallbackClickHandler
import ru.merkulyevsasha.coreandroid.common.newsadapter.ArticleShareCallbackClickHandler
import ru.merkulyevsasha.coreandroid.common.newsadapter.CommentLikeCallbackClickHandler
import ru.merkulyevsasha.coreandroid.common.newsadapter.CommentShareCallbackClickHandler
import ru.merkulyevsasha.coreandroid.presentation.ArticleLikeClickHandler
import timber.log.Timber

class ArticleCommentsPresenterImpl(
    private val articleCommentsInteractor: ArticleCommentsInteractor,
    articlesInteractor: ArticlesInteractor,
    private val newsDistributor: NewsDistributor
) : BasePresenterImpl<ArticleCommentsView>(),
    ArticleLikeCallbackClickHandler, ArticleShareCallbackClickHandler, CommentLikeCallbackClickHandler, CommentShareCallbackClickHandler {

    private val articleLikeClickHandler = ArticleLikeClickHandler(articlesInteractor,
        { view?.updateItem(it) },
        { view?.showError() })

    fun onFirstLoad(articleId: Int) {
        compositeDisposable.add(
            articleCommentsInteractor.getArticleComments(articleId)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { view?.showProgress() }
                .doAfterTerminate { view?.hideProgress() }
                .subscribe({
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
        newsDistributor.distribute(item)
    }

}