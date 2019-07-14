package ru.merkulyevsasha.core.repositories

import io.reactivex.Single
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.core.models.ArticleComment
import ru.merkulyevsasha.core.models.RssSource
import java.util.*

interface DatabaseRepository {
    fun removeOldNotUserActivityArticles(cleanDate: Date)
    fun removeOldUserActivityArticles(cleanDate: Date)
    fun getArticle(articleId: Int): Single<Article>
    fun getArticles(): Single<List<Article>>
    fun getArticleComments(articleId: Int): Single<List<ArticleComment>>

    fun saveRssSources(sources: List<RssSource>)
    fun getRssSources(): List<RssSource>
    fun deleteRssSources()

    fun addOrUpdateArticles(articles: List<Article>)
    fun updateArticle(article: Article)
    fun getUserActivityArticles(): Single<List<Article>>

    fun addOrUpdateArticleComments(comments: List<ArticleComment>)
    fun updateArticleComment(comment: ArticleComment, commentsCount: Int)
    fun searchArticles(searchText: String, byUserActivities: Boolean): Single<List<Article>>
}
