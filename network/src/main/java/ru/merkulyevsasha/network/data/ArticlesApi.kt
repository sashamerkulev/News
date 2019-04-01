package ru.merkulyevsasha.network.data

import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import ru.merkulyevsasha.network.models.ArticleCommentsResponse
import ru.merkulyevsasha.network.models.ArticleResponse

interface ArticlesApi {

    @GET("/articles")
    fun getArticles(): Single<List<ArticleResponse>>

    @GET("/articles/favorites")
    fun getFavoriteArticles(): Single<List<ArticleResponse>>

    @PUT("/articles/{articleId}/like")
    fun likeArticle(@Path("articleId") articleId: Long): Completable

    @PUT("/articles/{articleId}/dislike")
    fun dislikeArticle(@Path("articleId") articleId: Long): Completable

    @GET("/articles/{articleId}/comments")
    fun getArticleComments(@Path("articleId") articleId: Long): Single<List<ArticleCommentsResponse>>

    @PUT("/articles/{articleId}/comments/{commentId}/like")
    fun likeArticleComment(@Path("articleId") articleId: Long, @Path("commentId") commentId: Long): Completable

    @PUT("/articles/{articleId}/comments/{commentId}/dislike")
    fun dislikeArticleComment(@Path("articleId") articleId: Long, @Path("commentId") commentId: Long): Completable
}
