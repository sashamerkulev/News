package ru.merkulyevsasha.news.presentation.articledetails

import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.news.presentation.base.BaseView

interface ArticleDetailsView : BaseView {
    fun showProgress()
    fun hideProgress()
    fun showError()
    fun showItem(item: Article)
    fun updateItem(item: Article)
}
