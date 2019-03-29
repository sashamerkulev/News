package ru.merkulyevsasha.core

import io.reactivex.Completable
import io.reactivex.Single
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.core.models.ArticleComments

interface ArticlesApiRepository {
    fun getArticles(): Single<List<Article>>
    fun getFavoriteArticles(): Single<List<Article>>
    fun likeArticle(articleId: Long): Completable
    fun dislikeArticle(articleId: Long): Completable
    fun getArticleComments(articleId: Long): Single<List<ArticleComments>>
    fun likeArticleComment(articleId: Long, commentId: Long): Completable
    fun dislikeArticleComment(articleId: Long, commentId: Long): Completable
}

