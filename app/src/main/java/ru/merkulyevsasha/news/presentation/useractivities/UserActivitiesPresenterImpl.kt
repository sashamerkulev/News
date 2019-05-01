package ru.merkulyevsasha.news.presentation.useractivities

import ru.merkulyevsasha.core.domain.ArticlesInteractor
import ru.merkulyevsasha.core.presenters.FavoriteArticlesPresenter
import ru.merkulyevsasha.news.presentation.base.BasePresenter

class UserActivitiesPresenterImpl(private val articlesInteractor: ArticlesInteractor) : BasePresenter<UserActivitiesView>(), FavoriteArticlesPresenter {
}