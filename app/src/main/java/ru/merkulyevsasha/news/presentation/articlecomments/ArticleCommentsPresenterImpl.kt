package ru.merkulyevsasha.news.presentation.articlecomments

import io.reactivex.android.schedulers.AndroidSchedulers
import ru.merkulyevsasha.core.domain.ArticleCommentsInteractor
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.core.models.ArticleOrComment
import ru.merkulyevsasha.news.presentation.base.BasePresenterImpl
import ru.merkulyevsasha.news.presentation.common.newsadapter.ArticleCallbackClickHandler
import timber.log.Timber

class ArticleCommentsPresenterImpl(
    private val articleCommentsInteractor: ArticleCommentsInteractor
) : BasePresenterImpl<ArticleCommentsView>(), ArticleCallbackClickHandler {

    fun onFirstLoad(articleId: Int) {
        compositeDisposable.add(
            articleCommentsInteractor.getArticleComments(articleId)
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
    }

    override fun onArticleCliked(item: Article) {
    }

    override fun onLikeClicked(item: Article) {
    }

    override fun onCommentClicked(articleId: Int) {
    }

    override fun onDislikeClicked(item: Article) {
    }

    override fun onShareClicked(item: Article) {
    }
}