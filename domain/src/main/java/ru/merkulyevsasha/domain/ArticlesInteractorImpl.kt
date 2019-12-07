package ru.merkulyevsasha.domain

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.merkulyevsasha.core.domain.ArticlesInteractor
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.core.preferences.KeyValueStorage
import ru.merkulyevsasha.core.repositories.ArticlesApiRepository
import ru.merkulyevsasha.core.repositories.NewsDatabaseRepository
import java.util.*

class ArticlesInteractorImpl(
    private val articlesApiRepository: ArticlesApiRepository,
    private val keyValueStorage: KeyValueStorage,
    private val databaseRepository: NewsDatabaseRepository
) : ArticlesInteractor {

    companion object {
        private const val NOT_USER_ACTIVITIES_HOURS = 24 * 7 // week
        private const val USER_ACTIVITIES_HOURS = 24 * 30 // month
    }

    private val _rssSourceNameMap = mutableMapOf<String, String>()
    private val rssSourceNamesMap: Map<String, String>
        get() {
            if (_rssSourceNameMap.isEmpty()) {
                val map = databaseRepository.getRssSources().associateBy({ it.sourceId }, { it.sourceName })
                _rssSourceNameMap.putAll(map)
            }
            return _rssSourceNameMap
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
                        else Single.just(items)
                    }
            }
            .subscribeOn(Schedulers.io())
    }

    override fun getUserActivityArticles(): Single<List<Article>> {
        return databaseRepository.getUserActivityArticles()
            .subscribeOn(Schedulers.io())
    }

    override fun getSourceArticles(sourceName: String): Single<List<Article>> {
        return databaseRepository.getSourceArticles(sourceName)
            .subscribeOn(Schedulers.io())
    }

    override fun getArticle(articleId: Int): Single<Article> {
        return Single.fromCallable { rssSourceNamesMap }
            .flatMap { rssSourceNames ->
                articlesApiRepository.getArticle(articleId, rssSourceNames)
                    .doOnSuccess {
                        databaseRepository.updateArticle(it)
                    }
            }
            .subscribeOn(Schedulers.io())
    }

    override fun searchArticles(searchText: String?, byUserActivities: Boolean): Single<List<Article>> {
        return Single.fromCallable { searchText ?: "" }
            .flatMap { st: String ->
                if (st.isEmpty()) getArticles()
                else
                    databaseRepository.searchArticles(st, byUserActivities)
            }
            .subscribeOn(Schedulers.io())
    }

    override fun searchSourceArticles(sourceName: String, searchText: String?): Single<List<Article>> {
        return Single.fromCallable { searchText ?: "" }
            .flatMap { st: String ->
                if (st.isEmpty()) getSourceArticles(sourceName)
                else
                    databaseRepository.searchSourceArticles(sourceName, st)
            }
            .subscribeOn(Schedulers.io())
    }

    override fun refreshAndGetArticles(): Single<List<Article>> {
        return Single.fromCallable { keyValueStorage.getLastArticleReadDate() ?: Date(0) }
            .flatMap {
                Single.fromCallable { rssSourceNamesMap }
                    .flatMap { rssSourceNames ->
                        articlesApiRepository.getArticles(it, rssSourceNames)
                            .doOnSuccess { items ->
                                if (items.isNotEmpty()) {
                                    databaseRepository.addOrUpdateArticles(items)
                                    keyValueStorage.setLastArticleReadDate(Date())
                                }
                            }
                    }
                    .map { articles ->
                        val checkedSources = databaseRepository.getRssSources().filter { it.checked }
                        articles.filter { a -> checkedSources.any { it.sourceId == a.sourceId } }
                    }
                    .subscribeOn(Schedulers.io())
            }
    }

    override fun likeArticle(articleId: Int): Single<Article> {
        return Single.fromCallable { rssSourceNamesMap }
            .flatMap { rssSourceNames ->
                articlesApiRepository.likeArticle(articleId, rssSourceNames)
                    .doOnSuccess {
                        databaseRepository.updateArticle(it)
                    }
            }
            .subscribeOn(Schedulers.io())
    }

    override fun dislikeArticle(articleId: Int): Single<Article> {
        return Single.fromCallable { rssSourceNamesMap }
            .flatMap { rssSourceNames ->
                articlesApiRepository.dislikeArticle(articleId, rssSourceNames)
                    .doOnSuccess {
                        databaseRepository.updateArticle(it)
                    }
            }
            .subscribeOn(Schedulers.io())
    }
}
