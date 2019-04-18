package ru.merkulyevsasha.domain

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.merkulyevsasha.core.domain.SetupInteractor
import ru.merkulyevsasha.core.models.RssSource
import ru.merkulyevsasha.core.preferences.KeyValueStorage
import ru.merkulyevsasha.core.repositories.DatabaseRepository
import ru.merkulyevsasha.core.repositories.SetupApiRepository

class SetupInteractorImpl(
    private val preferences: KeyValueStorage,
    private val setupApiRepository: SetupApiRepository,
    private val databaseRepository: DatabaseRepository
) : SetupInteractor {
    override fun registerSetup(setupId: String, getFirebaseId: () -> String): Single<List<RssSource>> {
        return Single.fromCallable {  preferences.getSetupId() }
            .flatMap { savedSetupId ->
                if (savedSetupId.isEmpty()) setupApiRepository.registerSetup(setupId, getFirebaseId())
                    .doOnSuccess { token ->
                        preferences.setAccessToken(token.token)
                        preferences.setSetupId(setupId)
                    }
                else Single.fromCallable {  preferences.getAccessToken() }
            }
            .flatMap { setupApiRepository.getRssSources() }
            .doOnSuccess { sources ->
                databaseRepository.deleteRssSources()
                databaseRepository.saveRssSources(sources)
            }
            .subscribeOn(Schedulers.io())
    }
}