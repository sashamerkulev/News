package ru.merkulyevsasha.news

import android.content.Context
import ru.merkulyevsasha.core.routers.ApplicationRouter
import ru.merkulyevsasha.news.presentation.articlecomments.ArticleCommentsActivity
import ru.merkulyevsasha.news.presentation.articledetails.ArticleDetailsActivity

class ApplicationRouterImpl(private val applicationContext: Context) : ApplicationRouter {
    override fun showArticleDetails(articleId: Int) {
        ArticleDetailsActivity.show(applicationContext, articleId)
    }

    override fun showArticleComments(articleId: Int) {
        ArticleCommentsActivity.show(applicationContext, articleId)
    }
}