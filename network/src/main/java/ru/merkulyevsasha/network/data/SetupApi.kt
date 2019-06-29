package ru.merkulyevsasha.network.data

import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import ru.merkulyevsasha.network.models.RssSourceResponse
import ru.merkulyevsasha.network.models.TokenResponse

interface SetupApi {

    @FormUrlEncoded
    @POST("/setup/register")
    fun registerSetup(@Field("setupId") setupId: String, @Field("firebaseId") firebaseId: String): Single<TokenResponse>

    @GET("/setup/sources")
    fun getRssSources(): Single<List<RssSourceResponse>>

    @POST("/setup/firebase")
    @FormUrlEncoded
    fun updateFirebaseToken(@Field("firebaseId") firebaseId: String): Completable
}
