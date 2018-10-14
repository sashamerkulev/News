package ru.merkulyevsasha.news.data.db

import io.reactivex.Single
import ru.merkulyevsasha.news.data.db.entities.ArticleEntity
import java.util.*

interface DbDataSource {
    fun getLastPubDate(): Date?
    fun addListNews(items: List<ArticleEntity>)
    fun delete(navId: Int)
    fun deleteAll()
    fun readAllArticles(): Single<List<ArticleEntity>>
    fun readArticlesByNavId(navId: Int): Single<List<ArticleEntity>>
    fun search(searchTtext: String): Single<List<ArticleEntity>>
}