package ru.merkulyevsasha.core.repositories

import io.reactivex.Completable
import io.reactivex.Single
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.core.models.ArticleComments
import java.util.*

interface ArticlesApiRepository {
    fun getArticles(lastArticleReadDate: Date?): Single<List<Article>>
    fun getFavoriteArticles(): Single<List<Article>>
    fun likeArticle(articleId: Int): Single<Article>
    fun dislikeArticle(articleId: Int): Single<Article>
    fun getArticleComments(articleId: Int): Single<List<ArticleComments>>
    fun likeArticleComment(articleId: Int, commentId: Int): Completable
    fun dislikeArticleComment(articleId: Int, commentId: Int): Completable
}

