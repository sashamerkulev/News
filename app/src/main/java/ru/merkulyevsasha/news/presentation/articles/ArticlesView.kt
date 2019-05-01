package ru.merkulyevsasha.news.presentation.articles

import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.news.presentation.base.BaseView

interface ArticlesView : BaseView {
    fun showItems(items: List<Article>)
    fun showError()
}
