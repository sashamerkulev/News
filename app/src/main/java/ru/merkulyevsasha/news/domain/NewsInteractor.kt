package ru.merkulyevsasha.news.domain

import io.reactivex.Single
import ru.merkulyevsasha.news.models.Article

interface NewsInteractor {

    fun getFirstRun(): Single<Boolean>
    fun setFirstRunFlag()

    fun readAllArticles(): Single<List<Article>>
    fun readArticlesByNavId(navId: Int): Single<List<Article>>
    fun search(searchTtext: String): Single<List<Article>>

    fun refreshArticles(navId: Int): Single<List<Article>>
    fun readOrGetArticles(navId: Int): Single<List<Article>>
    fun refreshArticlesIfNeed(navId: Int): Single<List<Article>>
}
