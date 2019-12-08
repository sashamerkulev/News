package ru.merkulyevsasha.core.domain

import io.reactivex.Completable
import io.reactivex.Single
import ru.merkulyevsasha.core.models.ThemeEnum
import ru.merkulyevsasha.core.models.UserInfo
import ru.merkulyevsasha.core.models.UserProfile

interface UsersInteractor {
    fun getUserInfo(): Single<UserProfile>
    fun updateUser(name: String, phone: String): Single<UserInfo>
    fun uploadUserPhoto(profileFileName: String): Single<UserInfo>
    fun updateTheme(newTheme: ThemeEnum): Completable
}
