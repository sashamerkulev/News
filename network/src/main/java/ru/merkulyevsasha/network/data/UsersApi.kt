package ru.merkulyevsasha.network.data

import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part
import ru.merkulyevsasha.network.models.UserInfoResponse

interface UsersApi {

    @GET("/users/info")
    fun getUserInfo(): Single<UserInfoResponse>

    @FormUrlEncoded
    @PUT("/users/update")
    fun updateUser(@Field("name") name: String, @Field("phone") phone: String): Single<UserInfoResponse>

    @Multipart
    @PUT("/users/uploadPhoto")
    fun uploadUserPhoto(@Part image: MultipartBody.Part): Single<UserInfoResponse>
}
