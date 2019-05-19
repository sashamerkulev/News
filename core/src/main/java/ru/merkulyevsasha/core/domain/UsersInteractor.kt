package ru.merkulyevsasha.core.domain

import io.reactivex.Completable
import io.reactivex.Single
import ru.merkulyevsasha.core.models.UserInfo

interface UsersInteractor {
    fun getUserInfo(): Single<UserInfo>
    fun updateUser(name: String, phone: String): Completable
    fun uploadUserPhoto(profileFileName: String): Completable
}
