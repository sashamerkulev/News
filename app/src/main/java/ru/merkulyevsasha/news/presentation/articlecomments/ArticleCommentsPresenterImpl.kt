package ru.merkulyevsasha.news.presentation.articlecomments

import io.reactivex.android.schedulers.AndroidSchedulers
import ru.merkulyevsasha.core.domain.ArticleCommentsInteractor
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.core.models.ArticleComment
import ru.merkulyevsasha.core.models.ArticleOrComment
import ru.merkulyevsasha.core.preferences.KeyValueStorage
import ru.merkulyevsasha.news.BuildConfig
import ru.merkulyevsasha.news.presentation.base.BasePresenterImpl
import ru.merkulyevsasha.news.presentation.common.newsadapter.LikeArticleCallbackClickHandler
import ru.merkulyevsasha.news.presentation.common.newsadapter.ShareArticleCallbackClickHandler
import timber.log.Timber
import java.util.*

class ArticleCommentsPresenterImpl(
    private val articleCommentsInteractor: ArticleCommentsInteractor,
    private val keyValueStorage: KeyValueStorage
) : BasePresenterImpl<ArticleCommentsView>(), LikeArticleCallbackClickHandler, ShareArticleCallbackClickHandler {

    private val random = Random()

    fun onFirstLoad(articleId: Int) {
        compositeDisposable.add(
            articleCommentsInteractor.getArticleComments(articleId)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { view?.showProgress() }
                .doAfterTerminate { view?.hideProgress() }
                .subscribe({
                    val testResult = listOf<ArticleComment>(
                        testArticleComment(it.first, 1),
                        testArticleComment(it.first, 2),
                        testArticleComment(it.first, 3, true)
                    )
                    val result = listOf<ArticleOrComment>(it.first) + testResult // it.second
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
    }

    override fun onLikeClicked(item: Article) {
    }

    override fun onDislikeClicked(item: Article) {
    }

    override fun onShareClicked(item: Article) {
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