package ru.merkulyevsasha.sourcelist.domain

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.merkulyevsasha.core.domain.SourceInteractor
import ru.merkulyevsasha.core.models.RssSource
import ru.merkulyevsasha.core.repositories.NewsDatabaseRepository

class SourceInteractorImpl(private val databaseRepository: NewsDatabaseRepository) : SourceInteractor {
    override fun getRssSources(): Single<List<RssSource>> {
        return Single.fromCallable { databaseRepository.getRssSources() }
            .subscribeOn(Schedulers.io())
    }

    override fun updateRssSource(checked: Boolean, sourceId: String): Completable {
        return Completable.fromCallable { databaseRepository.updateRssSource(checked, sourceId) }
            .subscribeOn(Schedulers.io())
    }
}