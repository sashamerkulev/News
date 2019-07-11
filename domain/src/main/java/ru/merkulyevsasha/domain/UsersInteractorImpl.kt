package ru.merkulyevsasha.domain

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.merkulyevsasha.core.domain.UsersInteractor
import ru.merkulyevsasha.core.models.UserInfo
import ru.merkulyevsasha.core.preferences.KeyValueStorage
import ru.merkulyevsasha.core.repositories.UsersApiRepository

class UsersInteractorImpl(
    private val usersApiRepository: UsersApiRepository,
    private val keyValueStorage: KeyValueStorage
) : UsersInteractor {
    override fun getUserInfo(): Single<UserInfo> {
        return usersApiRepository.getUserInfo()
            .subscribeOn(Schedulers.io())
    }

    override fun updateUser(name: String, phone: String): Single<UserInfo> {
        return usersApiRepository.updateUser(name, phone)
            .doOnSuccess { keyValueStorage.saveNameAndPhone(name, phone) }
            .subscribeOn(Schedulers.io())
    }

    override fun uploadUserPhoto(profileFileName: String): Single<UserInfo> {
        return usersApiRepository.uploadUserPhoto(profileFileName)
            .doOnSuccess { keyValueStorage.saveProfileFileName(profileFileName) }
            .subscribeOn(Schedulers.io())
    }
}
