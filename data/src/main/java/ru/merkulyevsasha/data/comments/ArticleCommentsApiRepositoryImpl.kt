package ru.merkulyevsasha.data.comments

import io.reactivex.Completable
import io.reactivex.Single
import ru.merkulyevsasha.data.base.BaseApiRepository
import ru.merkulyevsasha.core.models.ArticleComment
import ru.merkulyevsasha.core.preferences.KeyValueStorage
import ru.merkulyevsasha.core.repositories.ArticleCommentsApiRepository
import ru.merkulyevsasha.network.data.CommentsApi
import ru.merkulyevsasha.network.mappers.ArticleCommentsMapper
import java.text.SimpleDateFormat
import java.util.*

class ArticleCommentsApiRepositoryImpl(
    sharedPreferences: KeyValueStorage,
    baseUrl: String
) : BaseApiRepository(sharedPreferences, baseUrl), ArticleCommentsApiRepository {

    private val articleCommentsMapper = ArticleCommentsMapper("bearer " + sharedPreferences.getAccessToken())

    private val api: CommentsApi = retrofit.create(CommentsApi::class.java)

    private val format = "yyyy-MM-dd'T'HH:mm:ss"
    private val simpleDateFormat = SimpleDateFormat(format, Locale.getDefault())

    override fun getArticleComments(articleId: Int, lastArticleCommentsReadDate: Date): Single<List<ArticleComment>> {
        return api.getArticleComments(articleId, simpleDateFormat.format(lastArticleCommentsReadDate))
            .flattenAsFlowable { it }
            .map { articleCommentsMapper.map(it) }
            .toList()
    }

    override fun addArticleComment(articleId: Int, comment: String): Single<ArticleComment> {
        return api.addArticleComment(articleId, comment)
            .map { articleCommentsMapper.map(it) }
    }

    override fun deleteArticleComment(commentId: Int): Completable {
        return api.deleteArticleComment(commentId)
    }

    override fun likeArticleComment(commentId: Int): Single<ArticleComment> {
        return api.likeArticleComment(commentId)
            .map { articleCommentsMapper.map(it) }
    }

    override fun dislikeArticleComment(commentId: Int): Single<ArticleComment> {
        return api.dislikeArticleComment(commentId)
            .map { articleCommentsMapper.map(it) }
    }
}