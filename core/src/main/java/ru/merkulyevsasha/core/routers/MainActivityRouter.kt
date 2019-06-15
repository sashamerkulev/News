package ru.merkulyevsasha.core.routers

interface MainActivityRouter {
    fun showMain()
    fun showSplash()
    fun showArticleDetails(articleId: Int)
    fun showArticleComments(articleId: Int)
}