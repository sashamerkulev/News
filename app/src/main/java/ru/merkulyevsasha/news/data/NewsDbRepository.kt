package ru.merkulyevsasha.news.data

import java.util.Date

import io.reactivex.Single
import ru.merkulyevsasha.news.models.Article

interface NewsDbRepository {

    val lastPubDate: Date?

    fun addListNews(items: List<Article>)

    fun delete(navId: Int)

    fun deleteAll()

    fun selectAll(): Single<List<Article>>

    fun selectNavId(navId: Int): Single<List<Article>>

    fun search(searchTtext: String): Single<List<Article>>
}
