package ru.merkulyevsasha.core.domain

import io.reactivex.Completable
import io.reactivex.Single
import ru.merkulyevsasha.core.models.ArticleComment

interface ArticleCommentsInteractor {
    fun commentArticle(articleId: Long, comment: String): Single<ArticleComment>
    fun likeArticleComment(): Completable
    fun dislikeArticleComment(): Completable
}
