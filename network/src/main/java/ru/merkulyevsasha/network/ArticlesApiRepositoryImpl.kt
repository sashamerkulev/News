package ru.merkulyevsasha.network

import com.facebook.stetho.okhttp3.StethoInterceptor
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import ru.merkulyevsasha.core.ArticlesApiRepository
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.core.models.ArticleComments
import ru.merkulyevsasha.network.data.ArticlesApi
import ru.merkulyevsasha.network.mappers.ArticleCommentsMapper
import ru.merkulyevsasha.network.mappers.ArticlesMapper

class ArticlesApiRepositoryImpl() : ArticlesApiRepository {

    private val articlesMapper = ArticlesMapper()
    private val articleCommentsMapper = ArticleCommentsMapper()

    private val api: ArticlesApi

    init {
        val builder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG_MODE) {
//            val httpLoggingInterceptor = HttpLoggingInterceptor()
//            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
//            builder.addInterceptor(httpLoggingInterceptor)
            builder.addNetworkInterceptor(StethoInterceptor())
        }
        builder.addNetworkInterceptor(LoggingInterceptor())
        builder.addInterceptor(TokenInterceptor())
        val client = builder.build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client)
            .build()

        api = retrofit.create(ArticlesApi::class.java)
    }

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