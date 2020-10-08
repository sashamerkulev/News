package ru.merkulyevsasha.articlecomments

import io.reactivex.android.schedulers.AndroidSchedulers
import ru.merkulyevsasha.core.ArticleDistributor
import ru.merkulyevsasha.core.domain.ArticleCommentsInteractor
import ru.merkulyevsasha.core.domain.ArticlesInteractor
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.core.models.ArticleComment
import ru.merkulyevsasha.core.models.ArticleOrComment
import ru.merkulyevsasha.coreandroid.base.BasePresenterImpl
import ru.merkulyevsasha.coreandroid.common.newsadapter.ArticleCommentLikeCallbackClickHandler
import ru.merkulyevsasha.coreandroid.common.newsadapter.ArticleCommentShareCallbackClickHandler
import ru.merkulyevsasha.coreandroid.common.newsadapter.ArticleLikeCallbackClickHandler
import ru.merkulyevsasha.coreandroid.common.newsadapter.ArticleShareCallbackClickHandler
import ru.merkulyevsasha.coreandroid.presentation.ArticleLikeClickHandler
import timber.log.Timber
import javax.inject.Inject

class ArticleCommentsPresenterImpl @Inject constructor(
    private val articleCommentsInteractor: ArticleCommentsInteractor,
    articlesInteractor: ArticlesInteractor,
    private val newsDistributor: ArticleDistributor
) : BasePresenterImpl<ArticleCommentsView>(),
    ArticleLikeCallbackClickHandler, ArticleShareCallbackClickHandler, ArticleCommentLikeCallbackClickHandler, ArticleCommentShareCallbackClickHandler {

    private val articleLikeClickHandler = ArticleLikeClickHandler(articlesInteractor,
        { addCommand { view?.updateItem(it) } },
        { view?.showError() })

    fun onFirstLoad(articleId: Int) {
        compositeDisposable.add(
            articleCommentsInteractor.getArticleComments(articleId)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { addCommand { view?.showProgress() } }
                .doAfterTerminate { addCommand { view?.hideProgress() } }
                .subscribe({
                    addCommand {
                        val result = listOf<ArticleOrComment>(it.first) + it.second
                        view?.showComments(result)
                    }
                }, {
                    Timber.e(it)
                    view?.showError()
                }))
    }

    fun onRefresh(articleId: Int) {
        compositeDisposable.add(
            articleCommentsInteractor.refreshAndGetArticleComments(articleId)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { addCommand { view?.showProgress() } }
                .doAfterTerminate { addCommand { view?.hideProgress() } }
                .subscribe({
                    addCommand {
                        val aaa = listOf<ArticleOrComment>(it.first) + it.second
                        view?.showComments(aaa)
                    }
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
                .doOnSubscribe { addCommand { view?.showProgress() } }
                .doAfterTerminate { addCommand { view?.hideProgress() } }
                .subscribe({
                    addCommand { view?.updateCommentItem(it) }
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

    override fun onArticleCommentLikeClicked(item: ArticleComment) {
        compositeDisposable.add(
            articleCommentsInteractor.likeArticleComment(item.commentId)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { addCommand { view?.showProgress() } }
                .doAfterTerminate { addCommand { view?.hideProgress() } }
                .subscribe({
                    addCommand { view?.updateCommentItem(it) }
                }, {
                    Timber.e(it)
                    view?.showError()
                }))
    }

    override fun onArticleCommentDislikeClicked(item: ArticleComment) {
        compositeDisposable.add(
            articleCommentsInteractor.dislikeArticleComment(item.commentId)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { addCommand { view?.showProgress() } }
                .doAfterTerminate { addCommand { view?.hideProgress() } }
                .subscribe({
                    addCommand { view?.updateCommentItem(it) }
                }, {
                    Timber.e(it)
                    view?.showError()
                }))
    }

    override fun onArticleCommentShareClicked(item: ArticleComment) {
        newsDistributor.distribute(item)
    }

}