package ru.merkulyevsasha.news.data.db

import io.reactivex.Single
import ru.merkulyevsasha.news.models.Article
import java.util.*

interface DbDataSource {
    fun getLastPubDate(): Date?
    fun addListNews(items: List<Article>)
    fun delete(navId: Int)
    fun deleteAll()
    fun selectAll(): Single<List<Article>>
    fun selectNavId(navId: Int): Single<List<Article>>
    fun search(searchTtext: String): Single<List<Article>>
}