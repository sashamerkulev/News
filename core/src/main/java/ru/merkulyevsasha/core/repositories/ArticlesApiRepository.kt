package ru.merkulyevsasha.core.repositories

import io.reactivex.Single
import ru.merkulyevsasha.core.models.Article
import java.util.*

interface ArticlesApiRepository {
    fun getArticles(lastArticleReadDate: Date, rssSourceNameMap : Map<String, String>): Single<List<Article>>
    fun likeArticle(articleId: Int, rssSourceNameMap : Map<String, String>): Single<Article>
    fun dislikeArticle(articleId: Int, rssSourceNameMap : Map<String, String>): Single<Article>
    fun getArticle(articleId: Int, rssSourceNameMap : Map<String, String>): Single<Article>
}
