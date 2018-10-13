package ru.merkulyevsasha.news.data

import java.util.Date

import io.reactivex.Single
import ru.merkulyevsasha.news.models.Article

interface NewsRepository {
    fun getLastPubDate(): Date?
    fun addListNews(items: List<Article>)
    fun delete(navId: Int)
    fun deleteAll()
    fun getAllArticles(): Single<List<Article>>
    fun getArticlesByNavId(navId: Int): Single<List<Article>>
    fun search(searchTtext: String): Single<List<Article>>

    fun getFirstRun(): Single<Boolean>
    fun setFirstRun()
    fun getProgress(): Single<Boolean>
    fun setProgress(progress: Boolean)

    fun readNewsAndSaveToDb(navId: Int): Single<List<Article>>
}