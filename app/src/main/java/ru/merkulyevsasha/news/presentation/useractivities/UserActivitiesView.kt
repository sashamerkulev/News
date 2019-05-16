package ru.merkulyevsasha.news.presentation.useractivities

import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.news.presentation.base.BaseView

interface UserActivitiesView : BaseView {
    fun showError()
    fun hideProgress()
    fun showProgress()
    fun showItems(items: List<Article>)
    fun updateItems(items: List<Article>)
    fun updateItem(item: Article)
    fun showArticleDetails(articleId: Int)
}
