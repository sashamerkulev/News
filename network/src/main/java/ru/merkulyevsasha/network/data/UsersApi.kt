package ru.merkulyevsasha.network.data

import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import ru.merkulyevsasha.network.models.UserInfoResponse
import ru.merkulyevsasha.network.models.UserRegisterResponse

interface UsersApi {

    @GET("/users/info")
    fun getUserInfo(): Single<UserInfoResponse>

    @POST("/users/register")
    fun registerUser(deviceId: String, firebaseId: String): Single<UserRegisterResponse>

    @POST("/users/update")
    fun updateUser(name: String, phone: String): Completable

    @Multipart
    @POST("/users/uploadphoto")
    fun uploadUserPhoto(): Completable
}

class UsersApiImpl(private val api: UsersApi) {

    fun getUserInfo(): Single<UserInfoResponse> {
        return api.getUserInfo()
    }

    fun registerUser(deviceId: String, firebaseId: String): Single<UserRegisterResponse> {
        return api.registerUser(deviceId, firebaseId)
    }

    fun updateUser(name: String, phone: String): Completable {
        return api.updateUser(name, phone)
    }

    fun uploadUserPhoto(): Completable {
        return api.uploadUserPhoto()
    }

}