package ru.merkulyevsasha.news.data.db.mappers

import ru.merkulyevsasha.news.data.Mapper
import ru.merkulyevsasha.news.data.db.entities.ArticleEntity
import ru.merkulyevsasha.news.models.Article

class ArticleMapper : Mapper<Article, ArticleEntity> {
    override fun map(item: Article): ArticleEntity {
        return ArticleEntity(
                item.sourceNavId,
                item.title,
                item.link,
                item.description ?: "",
                item.pubDate,
                item.category ?: "",
                item.search,
                item.pictureUrl ?: ""
        )
    }
}
