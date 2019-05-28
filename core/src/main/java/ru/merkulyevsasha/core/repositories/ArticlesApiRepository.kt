package ru.merkulyevsasha.core.repositories

import io.reactivex.Single
import ru.merkulyevsasha.core.models.Article
import java.util.*

interface ArticlesApiRepository {
    fun getArticles(lastArticleReadDate: Date?): Single<List<Article>>
    fun getUserActivityArticles(): Single<List<Article>>
    fun likeArticle(articleId: Int): Single<Article>
    fun dislikeArticle(articleId: Int): Single<Article>
    fun getArticle(articleId: Int): Single<Article>
}
