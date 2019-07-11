package ru.merkulyevsasha.domain

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.merkulyevsasha.core.domain.SetupInteractor
import ru.merkulyevsasha.core.models.RssSource
import ru.merkulyevsasha.core.preferences.KeyValueStorage
import ru.merkulyevsasha.core.repositories.DatabaseRepository
import ru.merkulyevsasha.core.repositories.SetupApiRepository
import java.util.*

class SetupInteractorImpl(
    private val preferences: KeyValueStorage,
    private val setupApiRepository: SetupApiRepository,
    private val databaseRepository: DatabaseRepository
) : SetupInteractor {

    override fun registerSetup(): Single<List<RssSource>> {
        return Single.fromCallable { preferences.getSetupId() }
            .flatMap { savedSetupId ->
                if (savedSetupId.isEmpty()) {
                    val setupId = UUID.randomUUID().toString()
                    setupApiRepository.registerSetup(setupId)
                        .doOnSuccess { token ->
                            preferences.setAccessToken(token.token)
                            preferences.setSetupId(setupId)
                        }
                        .map { it.token }
                } else Single.fromCallable { preferences.getAccessToken() }
            }
            .flatMap {
                setupApiRepository.getRssSources()
                    .doOnSuccess { sources ->
                        databaseRepository.deleteRssSources()
                        databaseRepository.saveRssSources(sources)
                    }
                    .onErrorResumeNext {
                        val sources = databaseRepository.getRssSources()
                        if (sources.isEmpty()) throw it
                        else Single.just(sources)
                    }
            }
            .subscribeOn(Schedulers.io())
    }

    override fun updateFirebaseToken(firebaseId: String): Completable {
        return setupApiRepository.updateFirebaseToken(firebaseId)
            .subscribeOn(Schedulers.io())
    }
}
