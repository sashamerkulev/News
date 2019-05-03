package ru.merkulyevsasha.domain

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.merkulyevsasha.core.domain.ArticlesInteractor
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.core.preferences.KeyValueStorage
import ru.merkulyevsasha.core.repositories.ArticlesApiRepository
import ru.merkulyevsasha.core.repositories.DatabaseRepository
import ru.merkulyevsasha.domain.mappers.SourceNameMapper
import java.util.*

class ArticlesInteractorImpl(
    private val articlesApiRepository: ArticlesApiRepository,
    private val keyValueStorage: KeyValueStorage,
    private val databaseRepository: DatabaseRepository,
    private val sourceNameMapper: SourceNameMapper
) : ArticlesInteractor {

    override fun getArticles(): Single<List<Article>> {
        return databaseRepository.getArticles()
            .flatMap { items ->
                if (items.isEmpty()) refreshAndGetArticles()
                else Single.just(sourceNameMapper.map(items))
            }
            .subscribeOn(Schedulers.io())
    }

    override fun refreshAndGetArticles(): Single<List<Article>> {
        return Single.fromCallable { keyValueStorage.getLastArticleReadDate() ?: Date(0) }
            .flatMap { articlesApiRepository.getArticles(it) }
            .doOnSuccess {
                databaseRepository.addOrUpdateArticles(it)
                keyValueStorage.setLastArticleReadDate(Date())
            }
            .map { sourceNameMapper.map(it) }
            .subscribeOn(Schedulers.io())
    }

    override fun getFavoriteArticles(): Single<List<Article>> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun likeArticle(articleId: Long): Completable {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun dislikeArticle(articleId: Long): Completable {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }
}