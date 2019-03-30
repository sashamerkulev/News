package ru.merkulyevsasha.domain

import io.reactivex.Completable
import io.reactivex.CompletableSource
import io.reactivex.Single
import ru.merkulyevsasha.core.domain.SetupInteractor
import ru.merkulyevsasha.core.preferences.SharedPreferences
import ru.merkulyevsasha.core.repositories.DatabaseRepository
import ru.merkulyevsasha.core.repositories.SetupApiRepository

class SetupInteractorImpl(
    private val preferences: SharedPreferences,
    private val setupApiRepository: SetupApiRepository,
    private val databaseRepository: DatabaseRepository
) : SetupInteractor {
    override fun registerSetup(setupId: String, getFirebaseId: () -> String): Completable {
        return preferences.getSetupId()
            .flatMap { savedSetupId ->
                if (savedSetupId.isEmpty()) setupApiRepository.registerSetup(setupId, getFirebaseId())
                    .doOnSuccess { token -> preferences.setAccessToken(token.token)
                        preferences.setSetupId(setupId)
                    }
                else Single.just(savedSetupId)
            }
            .flatMap { setupApiRepository.getRssSources() }
            .doOnSuccess { sources -> databaseRepository.saveRssSources(sources) }
            .flatMapCompletable { CompletableSource { } }
    }
}