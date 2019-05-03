package ru.merkulyevsasha.core.repositories

import io.reactivex.Single
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.core.models.ArticleComments
import ru.merkulyevsasha.core.models.RssSource

interface DatabaseRepository {
    fun getArticles(): Single<List<Article>>
    fun getArticleComments(): Single<List<ArticleComments>>

    fun saveRssSources(sources: List<RssSource>)
    fun getRssSources(): List<RssSource>
    fun deleteRssSources()
    fun addOrUpdateArticles(articles: List<Article>)
    fun updateLikeArticle(article: Article)
    fun updateDislikeArticle(article: Article)
}