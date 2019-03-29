package ru.merkulyevsasha.core.repositories

import io.reactivex.Single
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.core.models.ArticleComments

interface DatabaseRepository {
    fun getArticles(): Single<List<Article>>
    fun getArticleComments(): Single<List<ArticleComments>>
}