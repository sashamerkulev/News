package ru.merkulyevsasha.news.domain

import io.reactivex.Single
import ru.merkulyevsasha.news.models.Article

interface NewsInteractor {
    fun readArticlesByNavId(navId: Int, searchText: String? = null): Single<List<Article>>
    fun search(searchTtext: String): Single<List<Article>>

    fun refreshArticles(navId: Int): Single<List<Article>>

    fun startRefreshWorker(navId: Int, searchText: String?)
    fun startRefreshWorker()
    fun getProgress(): Single<Boolean>
    fun setProgress(progress: Boolean)
}
