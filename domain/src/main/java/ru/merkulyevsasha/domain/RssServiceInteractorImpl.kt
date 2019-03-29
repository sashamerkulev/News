package ru.merkulyevsasha.domain

import ru.merkulyevsasha.core.domain.RssServiceInteractor
import ru.merkulyevsasha.core.repositories.ArticlesApiRepository
import ru.merkulyevsasha.core.repositories.DatabaseRepository
import ru.merkulyevsasha.core.repositories.UsersApiRepository

class RssServiceInteractorImpl(
    private val articlesApiRepository: ArticlesApiRepository,
    private val usersApiRepository: UsersApiRepository,
    private val databaseRepository: DatabaseRepository
) : RssServiceInteractor {
}