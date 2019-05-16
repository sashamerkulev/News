package ru.merkulyevsasha.core.repositories

import io.reactivex.Completable
import io.reactivex.Single
import ru.merkulyevsasha.core.models.UserInfo

interface UsersApiRepository {
    fun getUserInfo(): Single<UserInfo>
    fun updateUser(name: String, phone: String): Completable
    fun uploadUserPhoto(): Completable
}
