package ru.merkulyevsasha.news.data.db.mappers

import ru.merkulyevsasha.news.data.Mapper
import ru.merkulyevsasha.news.data.db.entities.ArticleEntity
import ru.merkulyevsasha.news.models.Article

class ArticleEntityMapper : Mapper<ArticleEntity, Article> {
    override fun map(item: ArticleEntity): Article {
        return Article(
                item.id,
                item.sourceNavId,
                item.title,
                item.link,
                item.description,
                item.pubDate,
                item.category,
                item.search,
                item.pictureUrl
        )
    }
}
