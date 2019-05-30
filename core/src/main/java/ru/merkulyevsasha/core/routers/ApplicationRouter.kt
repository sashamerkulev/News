package ru.merkulyevsasha.core.routers

interface ApplicationRouter {
    fun showMainActivity()
    fun showArticleDetails(articleId: Int)
    fun showArticleComments(articleId: Int)
}