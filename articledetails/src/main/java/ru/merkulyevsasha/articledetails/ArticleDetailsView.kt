package ru.merkulyevsasha.articledetails

import ru.merkulyevsasha.core.base.BaseView
import ru.merkulyevsasha.core.models.Article

interface ArticleDetailsView : BaseView {
    fun showProgress()
    fun hideProgress()
    fun showError()
    fun showItem(item: Article)
    fun updateItem(item: Article)
}
