package ru.merkulyevsasha.sourcearticles

import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.coreandroid.base.BaseProgressView
import ru.merkulyevsasha.coreandroid.base.BaseView

interface SourceArticlesView : BaseView, BaseProgressView {
    fun showError()
    fun showItems(items: List<Article>)
    fun updateItems(items: List<Article>)
    fun updateItem(item: Article)
}
