package ru.merkulyevsasha.news.presentation.userinfo

import ru.merkulyevsasha.core.domain.UsersInteractor
import ru.merkulyevsasha.news.presentation.base.BasePresenterImpl

class UserInfoPresenterImpl(private val usersInteractor: UsersInteractor) : BasePresenterImpl<UserInfoView>() {
}