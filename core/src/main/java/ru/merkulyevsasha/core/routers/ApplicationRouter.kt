package ru.merkulyevsasha.core.routers

interface ApplicationRouter {
    fun showArticleDetails(articleId: Int)
    fun showArticleComments(articleId: Int)
}