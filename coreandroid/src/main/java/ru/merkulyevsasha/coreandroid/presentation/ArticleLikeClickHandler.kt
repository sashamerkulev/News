package ru.merkulyevsasha.coreandroid.presentation

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import ru.merkulyevsasha.core.domain.ArticlesInteractor
import ru.merkulyevsasha.core.models.Article

class ArticleLikeClickHandler(
    private val articlesInteractor: ArticlesInteractor,
    private val success: (Article) -> Unit,
    private val failure: () -> Unit
) {
    fun onArticleLikeClicked(articleId: Int): Disposable {
        return articlesInteractor.likeArticle(articleId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { newItem -> success(newItem) },
                {
                    failure()
                })
    }

    fun onArticleDislikeClicked(articleId: Int): Disposable {
        return articlesInteractor.dislikeArticle(articleId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { newItem -> success(newItem) },
                {
                    failure()
                })
    }
}