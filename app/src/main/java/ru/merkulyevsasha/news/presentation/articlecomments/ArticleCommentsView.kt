package ru.merkulyevsasha.news.presentation.articlecomments

import ru.merkulyevsasha.core.models.ArticleOrComment
import ru.merkulyevsasha.news.presentation.base.BaseView

interface ArticleCommentsView : BaseView {
    fun showProgress()
    fun hideProgress()
    fun showError()
    fun showComments(items: List<ArticleOrComment>)
}
