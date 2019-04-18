package ru.merkulyevsasha.domain

import io.reactivex.Completable
import io.reactivex.Single
import ru.merkulyevsasha.core.domain.ArticlesInteractor
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.core.repositories.ArticlesApiRepository
import ru.merkulyevsasha.core.repositories.DatabaseRepository

class ArticlesInteractorImpl(
    private val articlesApiRepository: ArticlesApiRepository,
    private val databaseRepository: DatabaseRepository
) : ArticlesInteractor {
    override fun getArticles(): Single<List<Article>> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getFavoriteArticles(): Single<List<Article>> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun likeArticle(articleId: Long): Completable {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun dislikeArticle(articleId: Long): Completable {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }
}