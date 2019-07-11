package ru.merkulyevsasha.coreandroid.presentation

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import ru.merkulyevsasha.core.domain.ArticlesInteractor
import ru.merkulyevsasha.core.models.Article
import timber.log.Timber

class ArticleLikeClickHandler(
    private val articlesInteractor: ArticlesInteractor,
    private val succes: (Article) -> Unit,
    private val failure: () -> Unit
) {
    fun onArticleLikeClicked(articleId: Int): Disposable {
        return articlesInteractor.likeArticle(articleId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { newItem -> succes(newItem) },
                {
                    Timber.e(it)
                    failure()
                })
    }

    fun onArticleDislikeClicked(articleId: Int): Disposable {
        return articlesInteractor.dislikeArticle(articleId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { newItem -> succes(newItem) },
                {
                    Timber.e(it)
                    failure()
                })
    }
}