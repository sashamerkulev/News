package ru.merkulyevsasha.network.data

import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import ru.merkulyevsasha.network.models.ArticleCommentsResponse
import ru.merkulyevsasha.network.models.ArticleResponse

interface ArticlesApi {

    @GET("/users/info")
    fun getArticles(): Single<List<ArticleResponse>>

    @GET("/articles/favorites")
    fun getFavoriteArticles(): Single<List<ArticleResponse>>

    @POST("/articles/{articleId}/like")
    fun likeArticle(@Path("articleId") articleId: Long): Completable

    @GET("/articles/{articleId}/dislike")
    fun dislikeArticle(@Path("articleId") articleId: Long): Completable

    @GET("/articles/{articleId}/comments")
    fun getArticleComments(@Path("articleId") articleId: Long): Single<List<ArticleCommentsResponse>>

    @GET("/articles/{articleId}/comments/{commentId}/like")
    fun likeArticleComment(@Path("articleId") articleId: Long, @Path("commentId") commentId: Long): Completable

    @GET("/articles/{articleId}/comments/{commentId}/dislike")
    fun dislikeArticleComment(@Path("articleId") articleId: Long, @Path("commentId") commentId: Long): Completable
}
