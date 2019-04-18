package ru.merkulyevsasha.network

import okhttp3.Interceptor
import okhttp3.Response
import ru.merkulyevsasha.core.preferences.SharedPreferences
import java.io.IOException

class TokenInterceptor(private val sharedPreferences: SharedPreferences) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val accessToken = sharedPreferences.getAccessToken();
        // 1. sign this request
        request = request.newBuilder()
            .header(AUTHORIZATION_KEY, "bearer $accessToken")
            .build()
        // 2. proceed with the request
        return chain.proceed(request)
    }

    companion object {

        private const val AUTHORIZATION_KEY = "Authorization"
    }
}
