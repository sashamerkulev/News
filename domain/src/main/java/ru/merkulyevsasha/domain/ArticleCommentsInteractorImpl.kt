package ru.merkulyevsasha.domain

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import ru.merkulyevsasha.core.domain.ArticleCommentsInteractor
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.core.models.ArticleComment
import ru.merkulyevsasha.core.repositories.ArticleCommentsApiRepository
import ru.merkulyevsasha.core.repositories.ArticlesApiRepository
import ru.merkulyevsasha.core.repositories.DatabaseRepository

class ArticleCommentsInteractorImpl(
    private val articlesApiRepository: ArticlesApiRepository,
    private val articleCommentsApiRepository: ArticleCommentsApiRepository,
    private val databaseRepository: DatabaseRepository
) : ArticleCommentsInteractor {
    override fun getArticleComments(articleId: Int): Single<Pair<Article, List<ArticleComment>>> {
        return Single.zip(
            articlesApiRepository.getArticle(articleId),
            articleCommentsApiRepository.getArticleComments(articleId),
            BiFunction { t1: Article, t2: List<ArticleComment> -> Pair(t1, t2) }
        )
            .subscribeOn(Schedulers.io())
    }

    override fun commentArticle(articleId: Int, comment: String): Single<ArticleComment> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun likeArticleComment(commentId: Int): Single<ArticleComment> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun dislikeArticleComment(commentId: Int): Single<ArticleComment> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteArticleComment(commentId: Int): Completable {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }
}
