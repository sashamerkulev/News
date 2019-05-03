package ru.merkulyevsasha.domain.mappers

import ru.merkulyevsasha.core.mappers.Mapper
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.core.repositories.DatabaseRepository

class SourceNameMapper(private val databaseRepository: DatabaseRepository) : Mapper<List<Article>, List<Article>> {

    private var rssSourceNameMap = mutableMapOf<String, String>()

    override fun map(item: List<Article>): List<Article> {
        if (rssSourceNameMap.isEmpty()) {
            val map = databaseRepository.getRssSources().associateBy({ it.name }, { it.title })
            rssSourceNameMap.putAll(map)
        }
        return item.map {
            Article(it.articleId,
                rssSourceNameMap[it.sourceName] ?: it.sourceName,
                it.title,
                it.link,
                it.description,
                it.pubDate,
                it.category,
                it.pictureUrl,
                it.usersLikeCount,
                it.usersDislikeCount,
                it.isUserLiked,
                it.isUserDisliked,
                it.isUserCommented
            )
        }
    }

}