package ru.merkulyevsasha.core.domain

import io.reactivex.Completable
import io.reactivex.Single
import ru.merkulyevsasha.core.models.RssSource

interface SetupInteractor {
    fun registerSetup(getFirebaseId: () -> String): Single<List<RssSource>>
    fun updateFirebaseToken(firebaseId: String): Completable
}
