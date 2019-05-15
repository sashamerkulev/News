package ru.merkulyevsasha.news.presentation.common.newsadapter

import ru.merkulyevsasha.core.models.Article

interface CallbackClickHandler {
    fun onArticleCliked(item: Article)
    fun onLikeClicked(item: Article)
    fun onCommentClicked(articleId: Int)
    fun onDislikeClicked(item: Article)
}