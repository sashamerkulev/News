package ru.merkulyevsasha.database

import android.arch.persistence.room.Room
import android.content.Context
import io.reactivex.Single
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.core.models.ArticleComments
import ru.merkulyevsasha.core.models.RssSource
import ru.merkulyevsasha.core.repositories.DatabaseRepository
import ru.merkulyevsasha.database.data.Database
import ru.merkulyevsasha.database.mappers.ArticleCommentsMapper
import ru.merkulyevsasha.database.mappers.ArticleMapper
import ru.merkulyevsasha.database.mappers.RssSourceMapper

class DatabaseRepositoryImpl(context: Context) : DatabaseRepository {

    private val articleMapper = ArticleMapper()
    private val articleCommentsMapper = ArticleCommentsMapper()
    private val rssSourceMapper = RssSourceMapper()

    private val database = Room
        .databaseBuilder(context, Database::class.java, BuildConfig.DB_NAME)
        .fallbackToDestructiveMigration()
        .build()

    override fun getArticles(): Single<List<Article>> {
        return database.articleDao.getArticles()
            .flattenAsFlowable { it }
            .map { articleMapper.map(it) }
            .toList()
    }

    override fun getArticleComments(): Single<List<ArticleComments>> {
        return database.articleCommentsDao.getArticleComments()
            .flattenAsFlowable { it }
            .map { articleCommentsMapper.map(it) }
            .toList()
    }

    override fun saveRssSources(sources: List<RssSource>) {
        database.setupDao.saveRssSources(sources.map { rssSourceMapper.map(it) })
    }

}