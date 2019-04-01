package ru.merkulyevsasha.network.data

import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PUT
import ru.merkulyevsasha.network.models.UserInfoResponse

interface UsersApi {

    @GET("/users/info")
    fun getUserInfo(): Single<UserInfoResponse>

    @FormUrlEncoded
    @PUT("/users/update")
    fun updateUser(@Field("name") name: String, @Field("phone") phone: String): Completable

    @Multipart
    @PUT("/users/uploadphoto")
    fun uploadUserPhoto(): Completable
}
