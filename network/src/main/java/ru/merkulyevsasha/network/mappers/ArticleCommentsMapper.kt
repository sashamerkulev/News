package ru.merkulyevsasha.network.mappers

import ru.merkulyevsasha.core.mappers.Mapper
import ru.merkulyevsasha.core.models.ArticleComments
import ru.merkulyevsasha.network.models.ArticleCommentsResponse

class ArticleCommentsMapper : Mapper<ArticleCommentsResponse, ArticleComments> {
    override fun map(item: ArticleCommentsResponse): ArticleComments {
        return ArticleComments()
    }
}