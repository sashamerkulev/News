package ru.merkulyevsasha.domain

import io.reactivex.Completable
import io.reactivex.Single
import ru.merkulyevsasha.core.domain.ArticleCommentsInteractor
import ru.merkulyevsasha.core.models.ArticleComment
import ru.merkulyevsasha.core.repositories.ArticlesApiRepository
import ru.merkulyevsasha.core.repositories.DatabaseRepository

class ArticleCommentsInteractorImpl(
    private val articlesApiRepository: ArticlesApiRepository,
    private val databaseRepository: DatabaseRepository
) : ArticleCommentsInteractor {
    override fun commentArticle(articleId: Long, comment: String): Single<ArticleComment> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun likeArticleComment(): Completable {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun dislikeArticleComment(): Completable {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }
}