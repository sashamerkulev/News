package ru.merkulyevsasha.network.data

import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import ru.merkulyevsasha.network.models.ArticleCommentResponse

interface CommentsApi {

    @GET("/articles/{articleId}/comments")
    fun getArticleComments(@Path("articleId") articleId: Int, @Query("lastCommentsReadDate") lastCommentsReadDate: String?): Single<List<ArticleCommentResponse>>

    @DELETE("/articles/comments/{commentId}")
    fun deleteArticleComment(@Path("commentId") commentId: Int): Completable

    @FormUrlEncoded
    @POST("/articles/{articleId}/comments")
    fun addArticleComment(@Path("articleId") articleId: Int, @Field("comments") comments: String): Single<ArticleCommentResponse>

    @PUT("/articles/comments/{commentId}/like")
    fun likeArticleComment(@Path("commentId") commentId: Int): Single<ArticleCommentResponse>

    @PUT("/articles/comments/{commentId}/dislike")
    fun dislikeArticleComment(@Path("commentId") commentId: Int): Single<ArticleCommentResponse>
}
