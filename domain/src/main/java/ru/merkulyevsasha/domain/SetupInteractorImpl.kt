package ru.merkulyevsasha.domain

import android.util.Log
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.merkulyevsasha.core.Logger
import ru.merkulyevsasha.core.domain.SetupInteractor
import ru.merkulyevsasha.core.models.RssSource
import ru.merkulyevsasha.core.preferences.KeyValueStorage
import ru.merkulyevsasha.core.repositories.NewsDatabaseRepository
import ru.merkulyevsasha.core.repositories.SetupApiRepository
import java.util.*

class SetupInteractorImpl(
    private val preferences: KeyValueStorage,
    private val setupApiRepository: SetupApiRepository,
    private val databaseRepository: NewsDatabaseRepository
) : SetupInteractor {

    override fun registerSetup(): Single<List<RssSource>> {
        return Single.fromCallable { preferences.getSetupId() }
            .flatMap { savedSetupId ->
                Logger.log("savedSetupId -> $savedSetupId")
                if (savedSetupId.isEmpty()) {
                    val setupId = UUID.randomUUID().toString()
                    Logger.log("setupId -> $setupId")
                    setupApiRepository.registerSetup(setupId)
                        .doOnSuccess { token ->
                            Logger.log("token -> $token")
                            preferences.setAccessToken(token.token)
                            preferences.setSetupId(setupId)
                        }
                        .map { it.token }
                } else Single.fromCallable { preferences.getAccessToken() }
            }
            .flatMap {
                setupApiRepository.getRssSources()
                    .doOnSuccess { sources ->
                        val oldSources = databaseRepository.getRssSources()
                        databaseRepository.deleteRssSources()
                        oldSources.forEach { oldSource ->
                            val newSource = sources.single { it.sourceId == oldSource.sourceId }
                            newSource.checked = oldSource.checked
                        }
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
