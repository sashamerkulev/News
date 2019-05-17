package ru.merkulyevsasha.news.presentation.articlecomments

import ru.merkulyevsasha.core.domain.ArticleCommentsInteractor
import ru.merkulyevsasha.news.presentation.base.BasePresenterImpl

class ArticleCommentsPresenterImpl(
    private val articleCommentsInteractor: ArticleCommentsInteractor
) : BasePresenterImpl<ArticleCommentsView>()