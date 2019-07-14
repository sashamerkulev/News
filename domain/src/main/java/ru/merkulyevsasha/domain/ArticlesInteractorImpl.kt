package ru.merkulyevsasha.domain

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

    companion object {
        private const val NOT_USER_ACTIVITIES_HOURS = 24 * 7
        private const val USER_ACTIVITIES_HOURS = 24 * 30
    }

    override fun getArticles(): Single<List<Article>> {
        return Single.fromCallable {
            val cleanDate = Calendar.getInstance()
            cleanDate.add(Calendar.HOUR, -NOT_USER_ACTIVITIES_HOURS)
            databaseRepository.removeOldNotUserActivityArticles(cleanDate.time)
            cleanDate.add(Calendar.HOUR, -USER_ACTIVITIES_HOURS)
            databaseRepository.removeOldUserActivityArticles(cleanDate.time)
        }
            .flatMap {
                databaseRepository.getArticles()
                    .flatMap { items ->
                        if (items.isEmpty()) refreshAndGetArticles()
                        else Single.just(items.map { sourceNameMapper.map(it) })
                    }
            }
            .subscribeOn(Schedulers.io())
    }

    override fun refreshAndGetArticles(): Single<List<Article>> {
        return Single.fromCallable { keyValueStorage.getLastArticleReadDate() ?: Date(0) }
            .flatMap {
                articlesApiRepository.getArticles(it)
                    .doOnSuccess { items ->
                        if (items.isNotEmpty()) {
                            databaseRepository.addOrUpdateArticles(items)
                            keyValueStorage.setLastArticleReadDate(Date())
                        }
                    }
            }
            .flattenAsFlowable { it }
            .map { sourceNameMapper.map(it) }
            .toList()
            .subscribeOn(Schedulers.io())
    }

    override fun searchArticles(searchText: String?, byUserActivities: Boolean): Single<List<Article>> {
        return Single.fromCallable { searchText ?: "" }
            .flatMap { st: String ->
                if (st.isEmpty()) getArticles()
                else
                    databaseRepository.searchArticles(st, byUserActivities)
                        .flattenAsFlowable { it }
                        .map { sourceNameMapper.map(it) }
                        .toList()
            }
            .subscribeOn(Schedulers.io())
    }

    override fun getUserActivityArticles(): Single<List<Article>> {
        return databaseRepository.getUserActivityArticles()
            .flattenAsFlowable { it }
            .map { sourceNameMapper.map(it) }
            .toList()
            .subscribeOn(Schedulers.io())
    }

    override fun likeArticle(articleId: Int): Single<Article> {
        return articlesApiRepository.likeArticle(articleId)
            .doOnSuccess {
                databaseRepository.updateArticle(it)
            }
            .map { sourceNameMapper.map(it) }
            .subscribeOn(Schedulers.io())
    }

    override fun dislikeArticle(articleId: Int): Single<Article> {
        return articlesApiRepository.dislikeArticle(articleId)
            .doOnSuccess {
                databaseRepository.updateArticle(it)
            }
            .map { sourceNameMapper.map(it) }
            .subscribeOn(Schedulers.io())
    }

    override fun getArticle(articleId: Int): Single<Article> {
        return articlesApiRepository.getArticle(articleId)
            .doOnSuccess {
                databaseRepository.updateArticle(it)
            }
            .map { sourceNameMapper.map(it) }
            .subscribeOn(Schedulers.io())
    }
}
