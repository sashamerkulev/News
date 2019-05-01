package ru.merkulyevsasha.news.presentation.userinfo

import ru.merkulyevsasha.core.domain.UsersInteractor
import ru.merkulyevsasha.core.presenters.UsersPresenter
import ru.merkulyevsasha.news.presentation.base.BasePresenter

class UserInfoPresenterImpl(private val usersInteractor: UsersInteractor) : BasePresenter<UserInfoView>(), UsersPresenter {
}