package ru.merkulyevsasha.network

import com.facebook.stetho.okhttp3.StethoInterceptor
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import ru.merkulyevsasha.core.UsersApiRepository
import ru.merkulyevsasha.core.models.UserInfo
import ru.merkulyevsasha.core.models.UserRegister
import ru.merkulyevsasha.network.data.UsersApi
import ru.merkulyevsasha.network.mappers.UserInfoMapper
import ru.merkulyevsasha.network.mappers.UserRegisterMapper

class UsersApiRepositoryImpl() : UsersApiRepository {

    private val userInfoMapper = UserInfoMapper()
    private val userRegisterMapper = UserRegisterMapper()

    private val api: UsersApi

    init {
        val builder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG_MODE) {
//            val httpLoggingInterceptor = HttpLoggingInterceptor()
//            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
//            builder.addInterceptor(httpLoggingInterceptor)
            builder.addNetworkInterceptor(StethoInterceptor())
        }
        builder.addNetworkInterceptor(LoggingInterceptor())
        builder.addInterceptor(TokenInterceptor())
        val client = builder.build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client)
            .build()

        api = retrofit.create(UsersApi::class.java)
    }

    override fun getUserInfo(): Single<UserInfo> {
        return api.getUserInfo()
            .map { userInfoMapper.map(it) }
    }

    override fun registerUser(deviceId: String, firebaseId: String): Single<UserRegister> {
        return api.registerUser(deviceId, firebaseId)
            .map { userRegisterMapper.map(it) }
    }

    override fun updateUser(name: String, phone: String): Completable {
        return api.updateUser(name, phone)
    }

    override fun uploadUserPhoto(): Completable {
        return api.uploadUserPhoto()
    }

}