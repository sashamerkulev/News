package ru.merkulyevsasha.core.repositories

import io.reactivex.Single
import ru.merkulyevsasha.core.models.RssSource
import ru.merkulyevsasha.core.models.Token

interface SetupApiRepository {
    fun registerSetup(setupId: String, firebaseId: String): Single<Token>
    fun getRssSources(): Single<List<RssSource>>
}
