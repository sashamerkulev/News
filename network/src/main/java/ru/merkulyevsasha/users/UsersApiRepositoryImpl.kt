package ru.merkulyevsasha.users

import io.reactivex.Single
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import ru.merkulyevsasha.base.BaseApiRepository
import ru.merkulyevsasha.core.models.UserInfo
import ru.merkulyevsasha.core.preferences.KeyValueStorage
import ru.merkulyevsasha.core.repositories.UsersApiRepository
import ru.merkulyevsasha.network.data.UsersApi
import ru.merkulyevsasha.network.mappers.UserInfoMapper
import java.io.File

class UsersApiRepositoryImpl(sharedPreferences: KeyValueStorage) : BaseApiRepository(sharedPreferences), UsersApiRepository {

    private val userInfoMapper: UserInfoMapper by lazy { UserInfoMapper("bearer " + sharedPreferences.getAccessToken()) }

    private val api: UsersApi = retrofit.create(UsersApi::class.java)

    override fun getUserInfo(): Single<UserInfo> {
        return api.getUserInfo()
            .map { userInfoMapper.map(it) }
    }

    override fun updateUser(name: String, phone: String): Single<UserInfo> {
        return api.updateUser(name, phone)
            .map { userInfoMapper.map(it) }
    }

    override fun uploadUserPhoto(profileFileName: String): Single<UserInfo> {
        return Single.fromCallable {
            val fileImage = File(profileFileName)
            val requestBody = RequestBody.create(MediaType.parse("image/*"), fileImage)
            MultipartBody.Part.createFormData("File", fileImage.name, requestBody)
        }
            .flatMap {
                api.uploadUserPhoto(it)
            }
            .map { userInfoMapper.map(it) }
    }
}