package ru.merkulyevsasha.news.presentation.users

import ru.merkulyevsasha.core.domain.UsersInteractor
import ru.merkulyevsasha.core.presenters.UsersPresenter
import ru.merkulyevsasha.news.presentation.base.BasePresenter

class UsersPresenterImpl(private val usersInteractor: UsersInteractor) : BasePresenter<UsersView>(), UsersPresenter {
}