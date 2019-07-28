package ru.merkulyevsasha.core

import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.core.models.ArticleComment

interface ArticleDistributor {
    fun distribute(item: Article)
    fun distribute(item: ArticleComment)
}