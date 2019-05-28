package ru.merkulyevsasha.database.mappers

import ru.merkulyevsasha.core.mappers.Mapper
import ru.merkulyevsasha.core.models.ArticleComment
import ru.merkulyevsasha.database.entities.ArticleCommentsEntity

class ArticleCommentsMapper : Mapper<ArticleCommentsEntity, ArticleComment> {
    override fun map(item: ArticleCommentsEntity): ArticleComment {
        return ArticleComment()
    }
}