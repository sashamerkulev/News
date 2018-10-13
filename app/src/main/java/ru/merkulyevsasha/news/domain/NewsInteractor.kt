package ru.merkulyevsasha.news.domain

import io.reactivex.Single
import ru.merkulyevsasha.news.models.Article

interface NewsInteractor {

    fun getFirstRun(): Single<Boolean>
    fun readNewsAndSaveToDb(navId: Int): Single<List<Article>>

    fun setFirstRunFlag()

    fun getAllArticles(): Single<List<Article>>

    fun getArticlesByNavId(navId: Int): Single<List<Article>>

    fun search(searchTtext: String): Single<List<Article>>

    fun needUpdate(): Boolean
}
