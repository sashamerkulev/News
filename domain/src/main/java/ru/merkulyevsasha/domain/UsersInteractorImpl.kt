package ru.merkulyevsasha.domain

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.merkulyevsasha.core.domain.UsersInteractor
import ru.merkulyevsasha.core.models.UserInfo
import ru.merkulyevsasha.core.preferences.KeyValueStorage
import ru.merkulyevsasha.core.repositories.DatabaseRepository
import ru.merkulyevsasha.core.repositories.UsersApiRepository

class UsersInteractorImpl(
    private val usersApiRepository: UsersApiRepository,
    private val databaseRepository: DatabaseRepository,
    private val keyValueStorage: KeyValueStorage
) : UsersInteractor {
    override fun getUserInfo(): Single<UserInfo> {
        return Single.fromCallable { UserInfo(
            keyValueStorage.getUserName(),
            keyValueStorage.getUserPhone(),
            keyValueStorage.getUserAvatarFileName()
        ) }
        .subscribeOn(Schedulers.io())
    }

    override fun updateUser(name: String, phone: String): Completable {
        return usersApiRepository.updateUser(name, phone)
            .doOnComplete { keyValueStorage.saveNameAndPhone(name, phone) }
            .subscribeOn(Schedulers.io())
    }

    override fun uploadUserPhoto(profileFileName: String): Completable {
        return usersApiRepository.uploadUserPhoto(profileFileName)
            .doOnComplete { keyValueStorage.saveProfileFileName(profileFileName) }
            .subscribeOn(Schedulers.io())
    }
}
