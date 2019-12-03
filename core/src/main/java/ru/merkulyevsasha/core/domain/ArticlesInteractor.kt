package ru.merkulyevsasha.core.domain

import io.reactivex.Single
import ru.merkulyevsasha.core.models.Article

interface ArticlesInteractor {
    fun getArticles(): Single<List<Article>>
    fun getUserActivityArticles(): Single<List<Article>>
    fun getSourceArticles(sourceName: String): Single<List<Article>>
    fun getArticle(articleId: Int): Single<Article>

    fun searchArticles(searchText: String?, byUserActivities: Boolean): Single<List<Article>>
    fun searchSourceArticles(sourceName: String, searchText: String?): Single<List<Article>>

    fun refreshAndGetArticles(): Single<List<Article>>

    fun likeArticle(articleId: Int): Single<Article>
    fun dislikeArticle(articleId: Int): Single<Article>
}
