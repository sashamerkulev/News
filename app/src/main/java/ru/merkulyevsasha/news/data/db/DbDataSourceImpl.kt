package ru.merkulyevsasha.news.data.db

import io.reactivex.Single
import ru.merkulyevsasha.news.data.db.entities.ArticleEntity
import java.util.*
import javax.inject.Inject

class DbDataSourceImpl @Inject
constructor(room: NewsDbRoom) : DbDataSource {

    private val dao by lazy {
        room.articleDao
    }

    override fun getLastPubDate(): Date? {
        val article = dao.getLastArticle()
        return article?.pubDate
    }

    override fun addListNews(items: List<ArticleEntity>) {
        dao.addListNews(items)
    }

    override fun delete(navId: Int) {
        dao.delete(navId)
    }

    override fun deleteAll() {
        dao.deleteAll()
    }

    override fun readAllArticles(): Single<List<ArticleEntity>> {
        return dao.selectAll()
    }

    override fun readArticlesByNavId(navId: Int): Single<List<ArticleEntity>> {
        return dao.selectNavId(navId)
    }

    override fun search(searchTtext: String): Single<List<ArticleEntity>> {
        return dao.search(searchTtext)
    }
}