package ru.merkulyevsasha.network.mappers

import ru.merkulyevsasha.core.mappers.Mapper
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.network.models.ArticleResponse

class ArticleMapper : Mapper<ArticleResponse, Article> {
    override fun map(item: ArticleResponse): Article {
        return Article(
            item.articleId,
            item.sourceName,
            item.title,
            item.link,
            item.description,
            item.pubDate,
            item.category,
            item.pictureUrl,
            item.likes,
            item.dislikes,
            item.comments,
            item.like > 0,
            item.dislike > 0,
            item.comment > 0
        )
    }
}