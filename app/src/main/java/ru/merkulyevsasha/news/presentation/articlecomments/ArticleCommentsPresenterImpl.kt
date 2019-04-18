package ru.merkulyevsasha.news.presentation.articlecomments

import ru.merkulyevsasha.core.domain.ArticleCommentsInteractor
import ru.merkulyevsasha.core.presenters.ArticleCommentsPresenter
import ru.merkulyevsasha.news.presentation.BasePresenter

class ArticleCommentsPresenterImpl(private val articleCommentsInteractor: ArticleCommentsInteractor) : BasePresenter<ArticleCommentsView>(), ArticleCommentsPresenter {
}