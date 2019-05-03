package ru.merkulyevsasha.news.presentation.articles

import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.news.presentation.base.BaseView

interface ArticlesView : BaseView {
    fun showError()
    fun hideProgress()
    fun showProgress()
    fun showItems(items: List<Article>)
    fun updateItems(items: List<Article>)
    fun setItemLike(item: Article)
    fun setItemDislike(item: Article)
}
