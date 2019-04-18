package ru.merkulyevsasha.network

import io.reactivex.Completable
import io.reactivex.Single
import ru.merkulyevsasha.core.models.UserInfo
import ru.merkulyevsasha.core.preferences.SharedPreferences
import ru.merkulyevsasha.core.repositories.UsersApiRepository
import ru.merkulyevsasha.network.data.UsersApi
import ru.merkulyevsasha.network.mappers.UserInfoMapper

class UsersApiRepositoryImpl(sharedPreferences: SharedPreferences) : BaseApiRepository(sharedPreferences), UsersApiRepository {

    private val userInfoMapper = UserInfoMapper()

    private val api: UsersApi = retrofit.create(UsersApi::class.java)

    override fun getUserInfo(): Single<UserInfo> {
        return api.getUserInfo()
            .map { userInfoMapper.map(it) }
    }

    override fun updateUser(name: String, phone: String): Completable {
        return api.updateUser(name, phone)
    }

    override fun uploadUserPhoto(): Completable {
        return api.uploadUserPhoto()
    }

}