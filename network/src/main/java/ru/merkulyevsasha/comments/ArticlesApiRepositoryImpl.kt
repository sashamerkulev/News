package ru.merkulyevsasha.comments

import io.reactivex.Completable
import io.reactivex.Single
import ru.merkulyevsasha.base.BaseApiRepository
import ru.merkulyevsasha.core.models.ArticleComment
import ru.merkulyevsasha.core.preferences.KeyValueStorage
import ru.merkulyevsasha.core.repositories.ArticleCommentsApiRepository
import ru.merkulyevsasha.network.data.ArticlesApi
import ru.merkulyevsasha.network.mappers.ArticleCommentsMapper
import ru.merkulyevsasha.network.mappers.ArticleMapper
import java.text.SimpleDateFormat
import java.util.*

class ArticlesApiRepositoryImpl(sharedPreferences: KeyValueStorage) : BaseApiRepository(sharedPreferences), ArticleCommentsApiRepository {

    private val articlesMapper = ArticleMapper()
    private val articleCommentsMapper = ArticleCommentsMapper()

    private val api: ArticlesApi = retrofit.create(ArticlesApi::class.java)

    private val format = "yyyy-MM-dd'T'HH:mm:ss"
    private val simpleDateFormat = SimpleDateFormat(format, Locale.getDefault())

    override fun getArticleComments(articleId: Int): Single<List<ArticleComment>> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun addArticleComment(articleId: Int, comment: String): Single<ArticleComment> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteArticleComment(commentId: Int): Completable {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
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