package ru.merkulyevsasha.base

import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.merkulyevsasha.core.preferences.SharedPreferences
import ru.merkulyevsasha.network.BuildConfig

open class BaseApiRepository(sharedPreferences: SharedPreferences) {

    internal val retrofit : Retrofit

    init {
        val builder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG_MODE) {
//            val httpLoggingInterceptor = HttpLoggingInterceptor()
//            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
//            builder.addInterceptor(httpLoggingInterceptor)
            builder.addNetworkInterceptor(StethoInterceptor())
        }
        builder.addNetworkInterceptor(LoggingInterceptor())
        builder.addInterceptor(TokenInterceptor(sharedPreferences))
        val client = builder.build()

        retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

}