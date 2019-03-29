package ru.merkulyevsasha.network.mappers

import ru.merkulyevsasha.core.mappers.Mapper
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.network.models.ArticleResponse

class ArticlesMapper: Mapper<ArticleResponse, Article> {
    override fun map(item: ArticleResponse): Article {
        return Article()
    }
}