package ru.merkulyevsasha.network

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class TokenInterceptor : Interceptor {

    //private final SharedPrefs sharedPrefs;

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val accessToken = "" // sharedPrefs.getAccessToken();
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
