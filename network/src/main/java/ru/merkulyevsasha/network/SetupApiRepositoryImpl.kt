package ru.merkulyevsasha.network

import io.reactivex.Single
import ru.merkulyevsasha.core.models.RssSource
import ru.merkulyevsasha.core.models.Token
import ru.merkulyevsasha.core.preferences.SharedPreferences
import ru.merkulyevsasha.core.repositories.SetupApiRepository
import ru.merkulyevsasha.network.data.UsersApi
import ru.merkulyevsasha.network.mappers.UserInfoMapper
import ru.merkulyevsasha.network.mappers.UserRegisterMapper

class SetupApiRepositoryImpl(sharedPreferences: SharedPreferences) : BaseApiRepository(sharedPreferences), SetupApiRepository {

    private val userInfoMapper = UserInfoMapper()
    private val userRegisterMapper = UserRegisterMapper()

    private val api: UsersApi = retrofit.create(UsersApi::class.java)

    override fun registerSetup(setupId: String, firebaseId: String): Single<Token> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getRssSources(): Single<List<RssSource>> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

}