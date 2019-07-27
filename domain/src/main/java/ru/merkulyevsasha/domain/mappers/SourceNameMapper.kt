package ru.merkulyevsasha.domain.mappers

import ru.merkulyevsasha.core.mappers.Mapper
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.core.repositories.NewsDatabaseRepository

class SourceNameMapper(private val databaseRepository: NewsDatabaseRepository) : Mapper<Article, Article> {

    private var rssSourceNameMap = mutableMapOf<String, String>()

    override fun map(item: Article): Article {
        if (rssSourceNameMap.isEmpty()) {
            val map = databaseRepository.getRssSources().associateBy({ it.name }, { it.title })
            rssSourceNameMap.putAll(map)
        }
        return Article(item.articleId,
            rssSourceNameMap[item.sourceName] ?: item.sourceName,
            item.title,
            item.link,
            item.description,
            item.pubDate,
            item.lastActivityDate,
            item.category,
            item.pictureUrl,
            item.usersLikeCount,
            item.usersDislikeCount,
            item.usersCommentCount,
            item.isUserLiked,
            item.isUserDisliked,
            item.isUserCommented
        )
    }
}
