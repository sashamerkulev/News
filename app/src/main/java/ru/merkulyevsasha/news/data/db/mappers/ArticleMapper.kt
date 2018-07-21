package ru.merkulyevsasha.news.data.db.mappers

import ru.merkulyevsasha.news.data.Mapper
import ru.merkulyevsasha.news.data.db.entities.ArticleEntity
import ru.merkulyevsasha.news.models.Article

class ArticleMapper : Mapper<Article, ArticleEntity> {
    override fun map(item: Article): ArticleEntity {
        val entity = ArticleEntity()
        entity.category = item.category ?: ""
        entity.description = item.description ?: ""
        entity.link = item.link
        entity.pictureUrl = item.pictureUrl ?: ""
        entity.pubDate = item.pubDate
        entity.sourceNavId = item.sourceNavId
        entity.title = item.title
        entity.search = item.search
        return entity
    }
}
