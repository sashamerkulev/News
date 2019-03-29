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

class ArticlesApiImpl(private val api: ArticlesApi) {

    fun getArticles(): Single<List<ArticleResponse>> {
        return api.getArticles()
    }

    fun getFavoriteArticles(): Single<List<ArticleResponse>> {
        return api.getFavoriteArticles()
    }

    fun likeArticle(articleId: Long): Completable {
        return api.likeArticle(articleId)
    }

    fun dislikeArticle(articleId: Long): Completable {
        return api.dislikeArticle(articleId)
    }

    fun getArticleComments(articleId: Long): Single<List<ArticleCommentsResponse>> {
        return api.getArticleComments(articleId)
    }

    fun likeArticleComment(articleId: Long, commentId: Long): Completable {
        return api.likeArticleComment(articleId, commentId)
    }

    fun dislikeArticleComment(articleId: Long, commentId: Long): Completable {
        return api.dislikeArticleComment(articleId, commentId)
    }

}