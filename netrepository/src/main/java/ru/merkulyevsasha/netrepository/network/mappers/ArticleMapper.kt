package ru.merkulyevsasha.netrepository.network.mappers

import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.network.models.ArticleResponse
import java.util.*

class ArticleMapper {
    fun map(item: ArticleResponse, rssSourceNameMap: Map<String, String>): Article {
        return Article(
            item.articleId,
            item.sourceId,
            rssSourceNameMap[item.sourceId] ?: item.sourceId,
            item.title,
            item.link,
            item.description,
            item.pubDate,
            item.lastActivityDate ?: Date(0),
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