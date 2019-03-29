package ru.merkulyevsasha.database.mappers

import ru.merkulyevsasha.core.mappers.Mapper
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.database.entities.ArticleEntity

class ArticleMapper: Mapper<ArticleEntity, Article> {
    override fun map(item: ArticleEntity): Article {
        return Article()
    }
}