package ru.merkulyevsasha.core.repositories

import io.reactivex.Single
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.core.models.ArticleComment
import ru.merkulyevsasha.core.models.RssSource
import java.util.*

interface NewsDatabaseRepository {
    fun getArticle(articleId: Int): Single<Article>
    fun getArticles(): Single<List<Article>>
    fun getArticleComments(articleId: Int): Single<List<ArticleComment>>
    fun getUserActivityArticles(): Single<List<Article>>
    fun getSourceArticles(sourceName: String): Single<List<Article>>

    fun searchArticles(searchText: String, byUserActivities: Boolean): Single<List<Article>>
    fun searchSourceArticles(sourceName: String, searchText: String): Single<List<Article>>

    fun removeOldNotUserActivityArticles(cleanDate: Date)
    fun removeOldUserActivityArticles(cleanDate: Date)

    fun deleteRssSources()
    fun saveRssSources(sources: List<RssSource>)
    fun getRssSources(): List<RssSource>

    fun addOrUpdateArticles(articles: List<Article>)
    fun updateArticle(article: Article)

    fun addOrUpdateArticleComments(comments: List<ArticleComment>)
    fun updateArticleComment(comment: ArticleComment, commentsCount: Int)
}
