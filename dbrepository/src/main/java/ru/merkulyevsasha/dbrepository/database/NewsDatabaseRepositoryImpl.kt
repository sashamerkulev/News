package ru.merkulyevsasha.dbrepository.database

import io.reactivex.Single
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.core.models.ArticleComment
import ru.merkulyevsasha.core.models.RssSource
import ru.merkulyevsasha.core.preferences.KeyValueStorage
import ru.merkulyevsasha.core.repositories.NewsDatabaseRepository
import ru.merkulyevsasha.dbrepository.database.mappers.ArticleCommentEntityMapper
import ru.merkulyevsasha.dbrepository.database.mappers.ArticleCommentMapper
import ru.merkulyevsasha.dbrepository.database.mappers.ArticleMapper
import ru.merkulyevsasha.dbrepository.database.mappers.RssSourceEntityMapper
import ru.merkulyevsasha.dbrepository.database.mappers.RssSourceMapper
import java.util.*

class NewsDatabaseRepositoryImpl(
    private val newsDatabaseSource: NewsDatabaseSource,
    keyValueStorage: KeyValueStorage,
    baseUrl: String
) : NewsDatabaseRepository {

    private val articleMapper = ArticleMapper()
    private val articleCommentEntityMapper by lazy { ArticleCommentEntityMapper("bearer " + keyValueStorage.getAccessToken(), baseUrl) }
    private val articleCommentMapper = ArticleCommentMapper()
    private val rssSourceMapper = RssSourceMapper()
    private val rssSourceEntityMapper = RssSourceEntityMapper()

    override fun getArticle(articleId: Int): Single<Article> {
        return newsDatabaseSource.getArticle(articleId)
    }

    override fun getArticles(): Single<List<Article>> {
        return newsDatabaseSource.getArticles()
    }

    override fun getArticleComments(articleId: Int): Single<List<ArticleComment>> {
        return newsDatabaseSource.getArticleComments(articleId)
            .flattenAsFlowable { it }
            .map { articleCommentEntityMapper.map(it) }
            .toList()
    }

    override fun getUserActivityArticles(): Single<List<Article>> {
        return newsDatabaseSource.getUserActivityArticles()
    }

    override fun getSourceArticles(sourceName: String): Single<List<Article>> {
        return newsDatabaseSource.getSourceArticles(sourceName)
    }

    override fun searchArticles(searchText: String, byUserActivities: Boolean): Single<List<Article>> {
        return Single.fromCallable { byUserActivities }
            .flatMap { ua ->
                if (ua) newsDatabaseSource.searchUserActivitiesArticles("%${searchText.toLowerCase(Locale.getDefault())}%")
                else newsDatabaseSource.searchArticles("%${searchText.toLowerCase(Locale.getDefault())}%")
            }
    }

    override fun searchSourceArticles(sourceName: String, searchText: String): Single<List<Article>> {
        return newsDatabaseSource.searchSourceArticles(sourceName, "%${searchText.toLowerCase(Locale.getDefault())}%")
    }

    override fun removeOldNotUserActivityArticles(cleanDate: Date) {
        newsDatabaseSource.removeOldNotUserActivityArticles(cleanDate)
    }

    override fun removeOldUserActivityArticles(cleanDate: Date) {
        newsDatabaseSource.removeOldUserActivityArticles(cleanDate)
    }

    override fun deleteRssSources() {
        newsDatabaseSource.deleteRssSources()
    }

    override fun saveRssSources(sources: List<RssSource>) {
        newsDatabaseSource.saveRssSources(sources.map { rssSourceMapper.map(it) })
    }

    override fun getRssSources(): List<RssSource> {
        return newsDatabaseSource.getRssSources()
            .map { rssSourceEntityMapper.map(it) }
    }

    override fun addOrUpdateArticles(articles: List<Article>) {
        newsDatabaseSource.addOrUpdateArticles(articles.map { articleMapper.map(it) })
    }

    override fun updateArticle(article: Article) {
        newsDatabaseSource.updateArticle(articleMapper.map(article))
    }

    override fun addOrUpdateArticleComments(comments: List<ArticleComment>) {
        newsDatabaseSource.addOrUpdateArticleComments(comments.map { articleCommentMapper.map(it) })
    }

    override fun updateArticleComment(comment: ArticleComment, commentsCount: Int) {
        newsDatabaseSource.updateArticleComment(articleCommentMapper.map(comment), commentsCount)
    }

    override fun updateRssSource(checked: Boolean, sourceId: String) {
        newsDatabaseSource.updateRssSource(checked, sourceId)
    }

}