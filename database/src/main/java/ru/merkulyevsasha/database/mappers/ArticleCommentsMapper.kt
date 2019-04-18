package ru.merkulyevsasha.database.mappers

import ru.merkulyevsasha.core.mappers.Mapper
import ru.merkulyevsasha.core.models.ArticleComments
import ru.merkulyevsasha.database.entities.ArticleCommentsEntity

class ArticleCommentsMapper : Mapper<ArticleCommentsEntity, ArticleComments> {
    override fun map(item: ArticleCommentsEntity): ArticleComments {
        return ArticleComments()
    }
}