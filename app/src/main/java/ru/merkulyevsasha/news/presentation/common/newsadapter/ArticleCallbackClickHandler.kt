package ru.merkulyevsasha.news.presentation.common.newsadapter

import ru.merkulyevsasha.core.models.Article

interface ArticleCallbackClickHandler {
    fun onArticleCliked(item: Article)
    fun onLikeClicked(item: Article)
    fun onCommentClicked(articleId: Int)
    fun onDislikeClicked(item: Article)
    fun onShareClicked(item: Article)
}