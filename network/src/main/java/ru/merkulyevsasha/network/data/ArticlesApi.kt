package ru.merkulyevsasha.network.data

import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import ru.merkulyevsasha.network.models.ArticleCommentsResponse
import ru.merkulyevsasha.network.models.ArticleResponse

interface ArticlesApi {

    @POST("/articles")
    @FormUrlEncoded
    fun getArticles(@Field("lastArticleReadDate") lastArticleReadDate: String?): Single<List<ArticleResponse>>

    @GET("/articles/favorites")
    fun getFavoriteArticles(): Single<List<ArticleResponse>>

    @PUT("/articles/{articleId}/like")
    fun likeArticle(@Path("articleId") articleId: Int): Single<ArticleResponse>

    @PUT("/articles/{articleId}/dislike")
    fun dislikeArticle(@Path("articleId") articleId: Int): Single<ArticleResponse>

    @GET("/articles/{articleId}")
    fun getArticle(@Path("articleId") articleId: Int): Single<ArticleResponse>

    @GET("/articles/{articleId}/comments")
    fun getArticleComments(@Path("articleId") articleId: Int): Single<List<ArticleCommentsResponse>>

    @PUT("/articles/{articleId}/comments/{commentId}/like")
    fun likeArticleComment(@Path("articleId") articleId: Int, @Path("commentId") commentId: Int): Completable

    @PUT("/articles/{articleId}/comments/{commentId}/dislike")
    fun dislikeArticleComment(@Path("articleId") articleId: Int, @Path("commentId") commentId: Int): Completable
}
