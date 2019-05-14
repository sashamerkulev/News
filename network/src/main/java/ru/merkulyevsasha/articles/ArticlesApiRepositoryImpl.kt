package ru.merkulyevsasha.articles

import io.reactivex.Completable
import io.reactivex.Single
import ru.merkulyevsasha.base.BaseApiRepository
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.core.models.ArticleComments
import ru.merkulyevsasha.core.preferences.KeyValueStorage
import ru.merkulyevsasha.core.repositories.ArticlesApiRepository
import ru.merkulyevsasha.network.data.ArticlesApi
import ru.merkulyevsasha.network.mappers.ArticleCommentsMapper
import ru.merkulyevsasha.network.mappers.ArticleMapper
import java.text.SimpleDateFormat
import java.util.*

class ArticlesApiRepositoryImpl(sharedPreferences: KeyValueStorage) : BaseApiRepository(sharedPreferences), ArticlesApiRepository {

    private val articlesMapper = ArticleMapper()
    private val articleCommentsMapper = ArticleCommentsMapper()

    private val api: ArticlesApi = retrofit.create(ArticlesApi::class.java)

    private val format = "yyyy-MM-dd'T'HH:mm:ss"
    private val simpleDateFormat = SimpleDateFormat(format, Locale.getDefault())

    override fun getArticles(lastArticleReadDate: Date?): Single<List<Article>> {
        return api.getArticles(simpleDateFormat.format(lastArticleReadDate))
            .flattenAsFlowable { it }
            .map { articlesMapper.map(it) }
            .toList()
    }

    override fun getFavoriteArticles(): Single<List<Article>> {
        return api.getFavoriteArticles()
            .flattenAsFlowable { it }
            .map { articlesMapper.map(it) }
            .toList()
    }

    override fun likeArticle(articleId: Int): Single<Article> {
        return api.likeArticle(articleId)
            .map { articlesMapper.map(it) }
    }

    override fun dislikeArticle(articleId: Int): Single<Article> {
        return api.dislikeArticle(articleId)
            .map { articlesMapper.map(it) }
    }

    override fun getArticle(articleId: Int): Single<Article> {
        return api.getArticle(articleId)
            .map { articlesMapper.map(it) }
    }

    override fun getArticleComments(articleId: Int): Single<List<ArticleComments>> {
        return api.getArticleComments(articleId)
            .flattenAsFlowable { it }
            .map { articleCommentsMapper.map(it) }
            .toList()
    }

    override fun likeArticleComment(articleId: Int, commentId: Int): Completable {
        return api.likeArticleComment(articleId, commentId)
    }

    override fun dislikeArticleComment(articleId: Int, commentId: Int): Completable {
        return api.dislikeArticleComment(articleId, commentId)
    }
}