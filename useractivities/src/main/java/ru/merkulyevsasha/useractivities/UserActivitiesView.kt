package ru.merkulyevsasha.useractivities

import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.core.base.BaseView

interface UserActivitiesView : BaseView {
    fun showError()
    fun hideProgress()
    fun showProgress()
    fun showItems(items: List<Article>)
    fun updateItems(items: List<Article>)
    fun updateItem(item: Article)
}
