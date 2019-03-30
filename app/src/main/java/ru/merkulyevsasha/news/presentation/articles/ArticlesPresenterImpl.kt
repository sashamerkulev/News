package ru.merkulyevsasha.news.presentation.articles

import ru.merkulyevsasha.core.domain.ArticlesInteractor
import ru.merkulyevsasha.core.presenters.ArticlesPresenter
import ru.merkulyevsasha.news.presentation.BasePresenter

class ArticlesPresenterImpl(private val articlesInteractor: ArticlesInteractor) : BasePresenter<ArticlesView>(), ArticlesPresenter {
}