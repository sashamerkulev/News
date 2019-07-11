package ru.merkulyevsasha.articledetails

import ru.merkulyevsasha.coreandroid.base.BaseView
import ru.merkulyevsasha.core.models.Article

interface ArticleDetailsView : ru.merkulyevsasha.coreandroid.base.BaseView {
    fun showProgress()
    fun hideProgress()
    fun showError()
    fun showItem(item: Article)
    fun updateItem(item: Article)
}
