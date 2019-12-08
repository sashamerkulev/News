package ru.merkulyevsasha.core.domain

import io.reactivex.Completable
import io.reactivex.Single
import ru.merkulyevsasha.core.models.RssSource

interface SourceInteractor {
    fun getRssSources() : Single<List<RssSource>>
    fun updateRssSource(checked: Boolean, sourceId: String): Completable
}