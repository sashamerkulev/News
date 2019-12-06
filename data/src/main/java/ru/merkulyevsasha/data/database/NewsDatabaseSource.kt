package ru.merkulyevsasha.data.database

import io.reactivex.Single
import ru.merkulyevsasha.database.entities.ArticleCommentEntity
import ru.merkulyevsasha.database.entities.ArticleEntity
import ru.merkulyevsasha.database.entities.RssSourceEntity
import java.util.*

interface NewsDatabaseSource {
    fun getArticles(): Single<List<ArticleEntity>>
    fun searchArticles(searchText: String): Single<List<ArticleEntity>>

    fun getArticle(articleId: Int): Single<ArticleEntity>

    fun getUserActivityArticles(): Single<List<ArticleEntity>>
    fun searchUserActivitiesArticles(searchText: String): Single<List<ArticleEntity>>

    fun getSourceArticles(sourceName: String): Single<List<ArticleEntity>>
    fun searchSourceArticles(sourceName: String, searchText: String): Single<List<ArticleEntity>>

    fun getArticleComments(articleId: Int): Single<List<ArticleCommentEntity>>

    fun removeOldNotUserActivityArticles(cleanDate: Date)
    fun removeOldUserActivityArticles(cleanDate: Date)

    fun deleteRssSources()
    fun saveRssSources(sources: List<RssSourceEntity>)
    fun getRssSources(): List<RssSourceEntity>

    fun addOrUpdateArticles(articles: List<ArticleEntity>)

    fun updateArticle(article: ArticleEntity)
    fun addOrUpdateArticleComments(comments: List<ArticleCommentEntity>)
    fun updateArticleComment(comment: ArticleCommentEntity, commentsCount: Int)
    fun updateRssSource(checked: Boolean, sourceId: String)
}