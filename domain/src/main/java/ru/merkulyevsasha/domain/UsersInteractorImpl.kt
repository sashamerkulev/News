package ru.merkulyevsasha.domain

import io.reactivex.Completable
import io.reactivex.Single
import ru.merkulyevsasha.core.domain.UsersInteractor
import ru.merkulyevsasha.core.models.UserInfo
import ru.merkulyevsasha.core.repositories.DatabaseRepository
import ru.merkulyevsasha.core.repositories.UsersApiRepository

class UsersInteractorImpl(
    private val usersApiRepository: UsersApiRepository,
    private val databaseRepository: DatabaseRepository
) : UsersInteractor {
    override fun getUserInfo(): Single<UserInfo> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun updateUser(name: String, phone: String): Completable {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun uploadUserPhoto(): Completable {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }
}
