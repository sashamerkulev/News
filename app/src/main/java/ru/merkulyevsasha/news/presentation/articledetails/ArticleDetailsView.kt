package ru.merkulyevsasha.news.presentation.articledetails

import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.news.presentation.base.BaseView

interface ArticleDetailsView : BaseView {
    fun showProgress()
    fun hideProgress()
    fun showItem(item: Article)
    fun showError()
    fun updateItem(item: Article)
}
