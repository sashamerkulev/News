package ru.merkulyevsasha.core.domain

import io.reactivex.Completable
import io.reactivex.Single
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.core.models.ArticleComment

interface ArticleCommentsInteractor {
    fun getArticleComments(articleId: Int): Single<Pair<Article, List<ArticleComment>>>
    fun refreshAndGetArticleComments(articleId: Int): Single<Pair<Article, List<ArticleComment>>>
    fun commentArticle(articleId: Int, comment: String): Single<ArticleComment>
    fun likeArticleComment(commentId: Int): Single<ArticleComment>
    fun dislikeArticleComment(commentId: Int): Single<ArticleComment>
    fun deleteArticleComment(commentId: Int): Completable
}
