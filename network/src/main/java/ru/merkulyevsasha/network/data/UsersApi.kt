package ru.merkulyevsasha.network.data

import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import ru.merkulyevsasha.network.models.UserInfoResponse

interface UsersApi {

    @GET("/users/info")
    fun getUserInfo(): Single<UserInfoResponse>

    @POST("/users/update")
    fun updateUser(name: String, phone: String): Completable

    @Multipart
    @POST("/users/uploadphoto")
    fun uploadUserPhoto(): Completable
}
