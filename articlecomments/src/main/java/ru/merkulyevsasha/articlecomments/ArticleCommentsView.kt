package ru.merkulyevsasha.articlecomments

import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.core.models.ArticleComment
import ru.merkulyevsasha.core.models.ArticleOrComment
import ru.merkulyevsasha.coreandroid.base.BaseView

interface ArticleCommentsView : ru.merkulyevsasha.coreandroid.base.BaseView {
    fun showProgress()
    fun hideProgress()
    fun showError()
    fun showComments(items: List<ArticleOrComment>)
    fun updateItem(item: Article)
    fun updateCommentItem(item: ArticleComment)
}
