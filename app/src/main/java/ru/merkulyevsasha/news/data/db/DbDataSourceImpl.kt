package ru.merkulyevsasha.news.data.db

import io.reactivex.Single
import ru.merkulyevsasha.news.data.MappersUtils
import ru.merkulyevsasha.news.data.db.entities.ArticleEntity
import ru.merkulyevsasha.news.data.db.mappers.ArticleEntityMapper
import ru.merkulyevsasha.news.data.db.mappers.ArticleMapper
import ru.merkulyevsasha.news.models.Article
import java.util.*
import javax.inject.Inject

class DbDataSourceImpl @Inject
constructor(room: NewsDbRoom) : DbDataSource {

    private val articleMapper: ArticleMapper = ArticleMapper()
    private val articleEntityMapper: ArticleEntityMapper = ArticleEntityMapper()

    private val dao by lazy {
        room.articleDao
    }

    override fun getLastPubDate(): Date? {
        val article = dao.getLastArticle()
        return article.pubDate
    }

    override fun addListNews(items: List<Article>) {
        dao.addListNews(*MappersUtils.convertToArray(items, articleMapper, ArticleEntity::class.java))
    }

    override fun delete(navId: Int) {
        dao.delete(navId)
    }

    override fun deleteAll() {
        dao.deleteAll()
    }

    override fun selectAll(): Single<List<Article>> {
        return dao.selectAll().flattenAsFlowable { t -> t }.map<Article> { articleEntityMapper.map(it) }.toList()
    }

    override fun selectNavId(navId: Int): Single<List<Article>> {
        return dao.selectNavId(navId).flattenAsFlowable { t -> t }.map<Article> { articleEntityMapper.map(it) }.toList()
    }

    override fun search(searchTtext: String): Single<List<Article>> {
        return dao.search(searchTtext).flattenAsFlowable { t -> t }.map<Article> { articleEntityMapper.map(it) }.toList()
    }
}