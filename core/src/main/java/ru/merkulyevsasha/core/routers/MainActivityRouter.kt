package ru.merkulyevsasha.core.routers

interface MainActivityRouter {
    fun showMainFragment()
    fun showArticleDetails(articleId: Int)
    fun showArticleComments(articleId: Int)
    fun showSourceArticles(sourceId: String, sourceName: String)
    fun showArticles()
    fun showSourceList()
    fun showUserActivities()
    fun showUserInfo()
}