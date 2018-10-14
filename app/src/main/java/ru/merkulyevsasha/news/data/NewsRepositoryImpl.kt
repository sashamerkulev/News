package ru.merkulyevsasha.news.data

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.merkulyevsasha.news.R
import ru.merkulyevsasha.news.data.db.DbDataSource
import ru.merkulyevsasha.news.data.http.HttpDataSource
import ru.merkulyevsasha.news.data.prefs.NewsSharedPreferences
import ru.merkulyevsasha.news.data.utils.NewsConstants
import ru.merkulyevsasha.news.models.Article
import ru.merkulyevsasha.news.models.ArticleMapper
import java.util.*
import javax.inject.Inject

class NewsRepositoryImpl @Inject
constructor(
    private val httpDataSource: HttpDataSource,
    private val dbDataSource: DbDataSource,
    private val newsSharedPreferences: NewsSharedPreferences,
    private val newsConstants: NewsConstants
) : NewsRepository {

    private val mapper: ArticleMapper = ArticleMapper()

    override fun getLastPubDate(): Date? {
        return dbDataSource.getLastPubDate()
    }

    override fun addListNews(items: List<Article>) {
        dbDataSource.addListNews(items.map { mapper.mapToArticleEntity(it) })
    }

    override fun delete(navId: Int) {
        dbDataSource.delete(navId)
    }

    override fun deleteAll() {
        dbDataSource.deleteAll()
    }

    override fun readAllArticles(): Single<List<Article>> {
        return dbDataSource.readAllArticles()
            .flattenAsFlowable { t -> t }
            .map<Article> { mapper.mapToArticle(it) }
            .toList()
            .subscribeOn(Schedulers.io())
    }

    override fun readArticlesByNavId(navId: Int): Single<List<Article>> {
        return dbDataSource.readArticlesByNavId(navId)
            .flattenAsFlowable { t -> t }
            .map<Article> { mapper.mapToArticle(it) }.toList()
            .subscribeOn(Schedulers.io())
    }

    override fun search(searchTtext: String): Single<List<Article>> {
        return dbDataSource.search(searchTtext)
            .flattenAsFlowable { t -> t }
            .map<Article> { mapper.mapToArticle(it) }
            .toList()
            .subscribeOn(Schedulers.io())
    }

    override fun readNewsAndSaveToDb(navId: Int): Single<List<Article>> {
        return Single.fromCallable {
            var items = mutableListOf<Article>()

            if (navId == R.id.nav_all) {
                for (entry in newsConstants.links.entries) {
                    try {
                        val articles = getArticles(entry.key, newsConstants.getLinkByNavId(entry.key))
                        updaDbAndSendNotification(articles, entry.key)
                        items.addAll(articles)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } else {
                items = getArticles(navId, newsConstants.getLinkByNavId(navId)) as MutableList<Article>
                updaDbAndSendNotification(items, navId)
            }
            //broadcastHelper.sendFinishBroadcast()

            items
        }
            .flattenAsFlowable { t -> t }
            .sorted { article, t1 -> t1.pubDate.compareTo(article.pubDate) }
            .toList()
            .cache()
            .subscribeOn(Schedulers.io())
    }

    override fun getFirstRun(): Single<Boolean> {
        return newsSharedPreferences.getFirstRun()
            .subscribeOn(Schedulers.io())
    }

    override fun setFirstRun() {
        newsSharedPreferences.setFirstRun()
    }

    override fun getProgress(): Single<Boolean> {
        return newsSharedPreferences.getProgress()
            .subscribeOn(Schedulers.io())
    }

    override fun setProgress(progress: Boolean) {
        newsSharedPreferences.setProgress(progress)
    }

    private fun getHttpData(navId: Int, url: String): List<Article> {
        return httpDataSource.getHttpData(navId, url)
    }

    private fun updaDbAndSendNotification(articles: List<Article>, key: Int) {
        delete(key)
        addListNews(articles)
        //broadcastHelper.sendUpdateBroadcast()
    }

    private fun getArticles(id: Int, url: String): List<Article> {
        var items: List<Article> = ArrayList()
        try {
            items = getHttpData(id, url)
            if (items.isNotEmpty()) {
                delete(id)
                addListNews(items)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return items
    }
}
