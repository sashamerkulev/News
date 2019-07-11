package ru.merkulyevsasha.core.domain

import io.reactivex.Single
import ru.merkulyevsasha.core.models.UserInfo

interface UsersInteractor {
    fun getUserInfo(): Single<UserInfo>
    fun updateUser(name: String, phone: String): Single<UserInfo>
    fun uploadUserPhoto(profileFileName: String): Single<UserInfo>
}
