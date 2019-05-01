package ru.merkulyevsasha.news.presentation.useractivities

import ru.merkulyevsasha.core.domain.ArticlesInteractor
import ru.merkulyevsasha.news.presentation.base.BasePresenterImpl

class UserActivitiesPresenterImpl(private val articlesInteractor: ArticlesInteractor) : BasePresenterImpl<UserActivitiesView>() {
}