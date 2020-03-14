package ru.merkulyevsasha.netrepository.network.setup

import io.reactivex.Completable
import io.reactivex.Single
import ru.merkulyevsasha.core.Logger
import ru.merkulyevsasha.core.models.RssSource
import ru.merkulyevsasha.core.models.Token
import ru.merkulyevsasha.core.preferences.KeyValueStorage
import ru.merkulyevsasha.core.repositories.SetupApiRepository
import ru.merkulyevsasha.netrepository.network.base.BaseApiRepository
import ru.merkulyevsasha.netrepository.network.mappers.RssSourceMapper
import ru.merkulyevsasha.network.data.SetupApi

class SetupApiRepositoryImpl(
    sharedPreferences: KeyValueStorage,
    baseUrl: String,
    debugMode: Boolean
) : BaseApiRepository(sharedPreferences, baseUrl, debugMode), SetupApiRepository {

    private val api: SetupApi = retrofit.create(SetupApi::class.java)

    private val rssSourceMapper = RssSourceMapper()

    override fun registerSetup(setupId: String): Single<Token> {
        Logger.log("registerSetup -> $setupId")
        return api.registerSetup(setupId, "")
            .map {
                Logger.log("registerSetup -> ${it.token}")
                Token(it.token)
            }
    }

    override fun updateFirebaseToken(firebaseId: String): Completable {
        return api.updateFirebaseToken(firebaseId)
    }

    override fun getRssSources(): Single<List<RssSource>> {
        return api.getRssSources()
            .flattenAsFlowable { it }
            .map { rssSourceMapper.map(it) }
            .toList()
    }
}