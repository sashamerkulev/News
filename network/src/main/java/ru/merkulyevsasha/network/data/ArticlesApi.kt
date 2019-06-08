package ru.merkulyevsasha.network.data

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import ru.merkulyevsasha.network.models.ArticleResponse

interface ArticlesApi {

    @GET("/articles")
    fun getArticles(@Query("lastArticleReadDate") lastArticleReadDate: String): Single<List<ArticleResponse>>

    @PUT("/articles/{articleId}/like")
    fun likeArticle(@Path("articleId") articleId: Int): Single<ArticleResponse>

    @PUT("/articles/{articleId}/dislike")
    fun dislikeArticle(@Path("articleId") articleId: Int): Single<ArticleResponse>

    @GET("/articles/{articleId}")
    fun getArticle(@Path("articleId") articleId: Int): Single<ArticleResponse>

}
