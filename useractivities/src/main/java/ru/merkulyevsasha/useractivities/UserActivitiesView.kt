package ru.merkulyevsasha.useractivities

import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.coreandroid.base.BaseProgressView
import ru.merkulyevsasha.coreandroid.base.BaseView

interface UserActivitiesView : BaseView, BaseProgressView {
    fun showError()
    fun showItems(items: List<Article>)
    fun updateItems(items: List<Article>)
    fun updateItem(item: Article)
}
