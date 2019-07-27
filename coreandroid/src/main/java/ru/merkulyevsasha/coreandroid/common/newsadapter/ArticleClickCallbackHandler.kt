package ru.merkulyevsasha.coreandroid.common.newsadapter

import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.core.models.ArticleComment

interface ArticleClickCallbackHandler {
    fun onArticleCliked(item: Article)
}

interface ArticleLikeCallbackClickHandler {
    fun onArticleLikeClicked(item: Article)
    fun onArticleDislikeClicked(item: Article)
}

interface ArticleCommentLikeCallbackClickHandler {
    fun onArticleCommentLikeClicked(item: ArticleComment)
    fun onArticleCommentDislikeClicked(item: ArticleComment)
}

interface ArticleCommentArticleCallbackClickHandler {
    fun onArticleCommentArticleClicked(articleId: Int)
}

interface ArticleShareCallbackClickHandler {
    fun onArticleShareClicked(item: Article)
}

interface ArticleCommentShareCallbackClickHandler {
    fun onArticleCommentShareClicked(item: ArticleComment)
}