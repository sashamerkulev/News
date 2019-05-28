package ru.merkulyevsasha.network.data

import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import ru.merkulyevsasha.network.models.ArticleCommentResponse
import ru.merkulyevsasha.network.models.ArticleResponse

interface ArticlesApi {

    @POST("/articles")
    @FormUrlEncoded
    fun getArticles(@Field("lastArticleReadDate") lastArticleReadDate: String?): Single<List<ArticleResponse>>

    @GET("/articles/users")
    fun getUserActivityArticles(): Single<List<ArticleResponse>>

    @PUT("/articles/{articleId}/like")
    fun likeArticle(@Path("articleId") articleId: Int): Single<ArticleResponse>

    @PUT("/articles/{articleId}/dislike")
    fun dislikeArticle(@Path("articleId") articleId: Int): Single<ArticleResponse>

    @GET("/articles/{articleId}")
    fun getArticle(@Path("articleId") articleId: Int): Single<ArticleResponse>

    @GET("/articles/{articleId}/comments")
    fun getArticleComments(@Path("articleId") articleId: Int): Single<List<ArticleCommentResponse>>

    @PUT("/articles/comments/{commentId}/like")
    fun likeArticleComment(@Path("commentId") commentId: Int): Single<ArticleCommentResponse>

    @PUT("/articles/comments/{commentId}/dislike")
    fun dislikeArticleComment(@Path("commentId") commentId: Int): Single<ArticleCommentResponse>
}
