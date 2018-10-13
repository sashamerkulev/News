package ru.merkulyevsasha.news.data.db

import io.reactivex.Single
import ru.merkulyevsasha.news.models.Article
import java.util.*

interface DbDataSource {
    fun getLastPubDate(): Date?
    fun addListNews(items: List<Article>)
    fun delete(navId: Int)
    fun deleteAll()
    fun getAllArticles(): Single<List<Article>>
    fun getArticlesByNavId(navId: Int): Single<List<Article>>
    fun search(searchTtext: String): Single<List<Article>>
}