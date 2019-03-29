package ru.merkulyevsasha.core

import io.reactivex.Completable
import io.reactivex.Single
import ru.merkulyevsasha.core.models.UserInfo
import ru.merkulyevsasha.core.models.UserRegister

interface UsersApiRepository {
    fun getUserInfo(): Single<UserInfo>
    fun registerUser(deviceId: String, firebaseId: String): Single<UserRegister>
    fun updateUser(name: String, phone: String): Completable
    fun uploadUserPhoto(): Completable
}

