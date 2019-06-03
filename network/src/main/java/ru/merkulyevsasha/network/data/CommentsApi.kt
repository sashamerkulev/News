package ru.merkulyevsasha.network.data

import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import ru.merkulyevsasha.network.models.ArticleCommentResponse

interface CommentsApi {

    @POST("/articles/{articleId}/comments")
    @FormUrlEncoded
    fun getArticleComments(@Path("articleId") articleId: Int, @Field("lastArticleReadDate") lastArticleReadDate: String?): Single<List<ArticleCommentResponse>>

    @DELETE("/articles/comments/{commentId}")
    fun deleteArticleComment(@Path("commentId") commentId: Int): Completable

    @POST("/articles/{articleId}/comment/")
    fun addArticleComment(@Path("articleId") articleId: Int, comment: String): Single<ArticleCommentResponse>

    @PUT("/articles/comments/{commentId}/like")
    fun likeArticleComment(@Path("commentId") commentId: Int): Single<ArticleCommentResponse>

    @PUT("/articles/comments/{commentId}/dislike")
    fun dislikeArticleComment(@Path("commentId") commentId: Int): Single<ArticleCommentResponse>
}
