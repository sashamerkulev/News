package ru.merkulyevsasha.news.data

import io.reactivex.Single
import ru.merkulyevsasha.news.data.db.NewsDbRoom
import ru.merkulyevsasha.news.data.db.entities.ArticleEntity
import ru.merkulyevsasha.news.data.db.mappers.ArticleEntityMapper
import ru.merkulyevsasha.news.data.db.mappers.ArticleMapper
import ru.merkulyevsasha.news.models.Article
import java.util.*
import javax.inject.Inject

class NewsDbRepositoryImpl @Inject
constructor(private val room: NewsDbRoom) : NewsDbRepository {

    private val articleMapper: ArticleMapper
    private val articleEntityMapper: ArticleEntityMapper

    override val lastPubDate: Date?
        get() {
            val dao = room.articleDao
            val article = dao.getLastArticle()
            return article.pubDate
        }

    init {
        articleMapper = ArticleMapper()
        articleEntityMapper = ArticleEntityMapper()
    }

    override fun addListNews(items: List<Article>) {
        val dao = room.articleDao
        dao.addListNews(*MappersUtils.convertToArray(items, articleMapper, ArticleEntity::class.java))
    }

    override fun delete(navId: Int) {
        val dao = room.articleDao
        dao.delete(navId)
    }

    override fun deleteAll() {
        val dao = room.articleDao
        dao.deleteAll()
    }

    override fun selectAll(): Single<List<Article>> {
        val dao = room.articleDao
        return dao.selectAll().flattenAsFlowable { t -> t }.map<Article> { articleEntityMapper.map(it) }.toList()
    }

    override fun selectNavId(navId: Int): Single<List<Article>> {
        val dao = room.articleDao
        return dao.selectNavId(navId).flattenAsFlowable { t -> t }.map<Article> { articleEntityMapper.map(it) }.toList()
    }

    override fun search(searchTtext: String): Single<List<Article>> {
        val dao = room.articleDao
        return dao.search(searchTtext).flattenAsFlowable { t -> t }.map<Article> { articleEntityMapper.map(it) }.toList()
    }

}
