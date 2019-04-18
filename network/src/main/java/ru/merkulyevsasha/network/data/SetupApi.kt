package ru.merkulyevsasha.network.data

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.POST
import ru.merkulyevsasha.network.models.RssSourceResponse
import ru.merkulyevsasha.network.models.TokenResponse

interface SetupApi {

    @GET("/setup/register")
    fun registerSetup(setupId: String, firebaseId: String): Single<TokenResponse>

    @POST("/setup/sources")
    fun getRssSources(): Single<List<RssSourceResponse>>
}
