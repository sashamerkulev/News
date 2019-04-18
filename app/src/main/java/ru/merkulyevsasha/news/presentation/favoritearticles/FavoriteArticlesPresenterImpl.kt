package ru.merkulyevsasha.news.presentation.favoritearticles

import ru.merkulyevsasha.core.domain.ArticlesInteractor
import ru.merkulyevsasha.core.presenters.FavoriteArticlesPresenter
import ru.merkulyevsasha.news.presentation.BasePresenter

class FavoriteArticlesPresenterImpl(private val articlesInteractor: ArticlesInteractor) : BasePresenter<FavoriteArticlesView>(), FavoriteArticlesPresenter {
}