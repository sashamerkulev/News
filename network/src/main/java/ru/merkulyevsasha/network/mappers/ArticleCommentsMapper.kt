package ru.merkulyevsasha.network.mappers

import ru.merkulyevsasha.core.mappers.Mapper
import ru.merkulyevsasha.core.models.ArticleComment
import ru.merkulyevsasha.network.BuildConfig
import ru.merkulyevsasha.network.models.ArticleCommentResponse

class ArticleCommentsMapper(private val authorization: String) : Mapper<ArticleCommentResponse, ArticleComment> {
    override fun map(item: ArticleCommentResponse): ArticleComment {
        return ArticleComment(
            item.articleId,
            item.commentId,
            item.userId,
            item.userName ?: "",
            item.pubDate,
            item.comment,
            item.status,
            item.likes,
            item.dislike,
            item.like > 0,
            item.dislike > 0,
            item.owner == 1,
            BuildConfig.API_URL + "/users/${item.userId}/downloadPhoto",
            authorization
        )
    }
}