package ru.merkulyevsasha.userinfo.domain

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.merkulyevsasha.core.domain.UsersInteractor
import ru.merkulyevsasha.core.models.ThemeEnum
import ru.merkulyevsasha.core.models.UserInfo
import ru.merkulyevsasha.core.models.UserProfile
import ru.merkulyevsasha.core.preferences.KeyValueStorage
import ru.merkulyevsasha.core.repositories.NewsDatabaseRepository
import ru.merkulyevsasha.core.repositories.UsersApiRepository

class UsersInteractorImpl(
    private val usersApiRepository: UsersApiRepository,
    private val keyValueStorage: KeyValueStorage,
    private val databaseRepository: NewsDatabaseRepository
) : UsersInteractor {
    override fun getUserInfo(): Single<UserProfile> {
        return usersApiRepository.getUserInfo()
            .map { userInfo ->
                UserProfile(userInfo, keyValueStorage.getUserProfileTheme(), databaseRepository.getRssSources())
            }
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

    override fun updateTheme(newTheme: ThemeEnum): Completable {
        return Completable.fromCallable { keyValueStorage.setUserProfileTheme(newTheme) }
            .subscribeOn(Schedulers.io())
    }

}