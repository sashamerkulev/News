package ru.merkulyevsasha.network

import io.reactivex.Completable
import io.reactivex.Single
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.core.models.ArticleComments
import ru.merkulyevsasha.core.preferences.SharedPreferences
import ru.merkulyevsasha.core.repositories.ArticlesApiRepository
import ru.merkulyevsasha.network.data.ArticlesApi
import ru.merkulyevsasha.network.mappers.ArticleCommentsMapper
import ru.merkulyevsasha.network.mappers.ArticleMapper

class ArticlesApiRepositoryImpl(sharedPreferences: SharedPreferences) : BaseApiRepository(sharedPreferences), ArticlesApiRepository {

    private val articlesMapper = ArticleMapper()
    private val articleCommentsMapper = ArticleCommentsMapper()

    private val api: ArticlesApi = retrofit.create(ArticlesApi::class.java)

    override fun getArticles(): Single<List<Article>> {
        return api.getArticles()
            .flattenAsFlowable { it }
            .map { Article() }
            .toList()
    }

    override fun getFavoriteArticles(): Single<List<Article>> {
        return api.getFavoriteArticles()
            .flattenAsFlowable { it }
            .map { articlesMapper.map(it) }
            .toList()
    }

    override fun likeArticle(articleId: Long): Completable {
        return api.likeArticle(articleId)
    }

    override fun dislikeArticle(articleId: Long): Completable {
        return api.dislikeArticle(articleId)
    }

    override fun getArticleComments(articleId: Long): Single<List<ArticleComments>> {
        return api.getArticleComments(articleId)
            .flattenAsFlowable { it }
            .map { articleCommentsMapper.map(it) }
            .toList()
    }

    override fun likeArticleComment(articleId: Long, commentId: Long): Completable {
        return api.likeArticleComment(articleId, commentId)
    }

    override fun dislikeArticleComment(articleId: Long, commentId: Long): Completable {
        return api.dislikeArticleComment(articleId, commentId)
    }

}