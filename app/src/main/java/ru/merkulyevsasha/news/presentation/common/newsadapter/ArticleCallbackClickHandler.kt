package ru.merkulyevsasha.news.presentation.common.newsadapter

import ru.merkulyevsasha.core.models.Article

interface ArticleCallbackClickHandler {
    fun onArticleCliked(item: Article)
}

interface LikeArticleCallbackClickHandler {
    fun onLikeClicked(item: Article)
    fun onDislikeClicked(item: Article)
}

interface LikeCommentCallbackClickHandler {
    fun onLikeClicked(item: Article)
    fun onDislikeClicked(item: Article)
}

interface CommentArticleCallbackClickHandler {
    fun onCommentClicked(articleId: Int)
}

interface ShareArticleCallbackClickHandler {
    fun onShareClicked(item: Article)
}

interface ShareCommentCallbackClickHandler {
    fun onShareClicked(item: Article)
}