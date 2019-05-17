package ru.merkulyevsasha.core.domain

import io.reactivex.Single
import ru.merkulyevsasha.core.models.RssSource

interface SetupInteractor {
    fun registerSetup(setupId: String, getFirebaseId: () -> String): Single<List<RssSource>>
}
