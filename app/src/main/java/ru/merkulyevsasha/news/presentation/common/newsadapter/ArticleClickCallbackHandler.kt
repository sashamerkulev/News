package ru.merkulyevsasha.news.presentation.common.newsadapter

import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.core.models.ArticleComment

interface ArticleClickCallbackHandler {
    fun onArticleCliked(item: Article)
}

interface ArticleLikeCallbackClickHandler {
    fun onArticleLikeClicked(item: Article)
    fun onArticleDislikeClicked(item: Article)
}

interface CommentLikeCallbackClickHandler {
    fun onCommentLikeClicked(item: ArticleComment)
    fun onCommentDislikeClicked(item: ArticleComment)
}

interface CommentArticleCallbackClickHandler {
    fun onCommentArticleClicked(articleId: Int)
}

interface ArticleShareCallbackClickHandler {
    fun onArticleShareClicked(item: Article)
}

interface CommentShareCallbackClickHandler {
    fun onCommentShareClicked(item: ArticleComment)
}