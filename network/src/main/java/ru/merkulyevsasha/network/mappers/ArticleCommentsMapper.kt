package ru.merkulyevsasha.network.mappers

import ru.merkulyevsasha.core.mappers.Mapper
import ru.merkulyevsasha.core.models.ArticleComment
import ru.merkulyevsasha.network.models.ArticleCommentResponse

class ArticleCommentsMapper : Mapper<ArticleCommentResponse, ArticleComment> {
    override fun map(item: ArticleCommentResponse): ArticleComment {
        return ArticleComment()
    }
}