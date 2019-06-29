package ru.merkulyevsasha.setup

import io.reactivex.Completable
import io.reactivex.Single
import ru.merkulyevsasha.base.BaseApiRepository
import ru.merkulyevsasha.core.models.RssSource
import ru.merkulyevsasha.core.models.Token
import ru.merkulyevsasha.core.preferences.KeyValueStorage
import ru.merkulyevsasha.core.repositories.SetupApiRepository
import ru.merkulyevsasha.network.data.SetupApi
import ru.merkulyevsasha.network.mappers.RssSourceMapper

class SetupApiRepositoryImpl(sharedPreferences: KeyValueStorage) : BaseApiRepository(sharedPreferences), SetupApiRepository {

    private val api: SetupApi = retrofit.create(SetupApi::class.java)

    private val rssSourceMapper = RssSourceMapper()

    override fun registerSetup(setupId: String, firebaseId: String): Single<Token> {
        return api.registerSetup(setupId, firebaseId)
            .map { Token(it.token) }
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