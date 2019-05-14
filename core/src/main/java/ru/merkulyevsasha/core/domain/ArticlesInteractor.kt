package ru.merkulyevsasha.core.domain

import io.reactivex.Single
import ru.merkulyevsasha.core.models.Article

interface ArticlesInteractor {
    fun refreshAndGetArticles(): Single<List<Article>>
    fun getArticles(): Single<List<Article>>
    fun getFavoriteArticles(): Single<List<Article>>
    fun likeArticle(articleId: Int): Single<Article>
    fun dislikeArticle(articleId: Int): Single<Article>
    fun getArticle(articleId: Int): Single<Article>
}