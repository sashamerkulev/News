package ru.merkulyevsasha.news.domain


import java.util.ArrayList
import java.util.Date

import javax.inject.Inject

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.merkulyevsasha.news.R
import ru.merkulyevsasha.news.data.NewsDbRepository
import ru.merkulyevsasha.news.data.http.HttpReader
import ru.merkulyevsasha.news.data.prefs.NewsSharedPreferences
import ru.merkulyevsasha.news.data.utils.NewsConstants
import ru.merkulyevsasha.news.helpers.BroadcastHelper
import ru.merkulyevsasha.news.models.Article

class NewsInteractorImpl @Inject
constructor(private val newsConstants: NewsConstants, private val reader: HttpReader,
            private val prefs: NewsSharedPreferences, private val db: NewsDbRepository, private val broadcastHelper: BroadcastHelper) : NewsInteractor {

    override val firstRunFlag: Single<Boolean>
        @Override
        get() = prefs
                .firstRunFlag
                .subscribeOn(Schedulers.io())

    @Override
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
            broadcastHelper.sendFinishBroadcast()

            items
        }
                .flattenAsFlowable { t -> t }
                .sorted { article, t1 -> t1.pubDate.compareTo(article.pubDate) }
                .toList()
                .cache()
                .subscribeOn(Schedulers.io())
    }

    @Override
    override fun setFirstRunFlag() {
        prefs.setFirstRunFlag()
    }

    @Override
    override fun selectAll(): Single<List<Article>> {
        return db.selectAll()
                .subscribeOn(Schedulers.io())
    }

    @Override
    override fun selectNavId(navId: Int): Single<List<Article>> {
        return db.selectNavId(navId)
                .subscribeOn(Schedulers.io())
    }

    @Override
    override fun search(searchTtext: String): Single<List<Article>> {
        return db.search(searchTtext)
                .subscribeOn(Schedulers.io())
    }

    @Override
    override fun needUpdate(): Boolean {
        val lastpubDate = db.lastPubDate ?: return true
        val nowdate = Date()
        val diffMinutes = (nowdate.time / 60000 - lastpubDate.time / 60000)
        return diffMinutes > 30
    }

    private fun getArticles(id: Int, url: String): List<Article> {
        var items: List<Article> = ArrayList()
        try {
            items = reader.getHttpData(id, url)
            if (items.isNotEmpty()) {
                db.delete(id)
                db.addListNews(items)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return items
    }

    private fun updaDbAndSendNotification(articles: List<Article>, key: Int) {
        db.delete(key)
        db.addListNews(articles)
        broadcastHelper.sendUpdateBroadcast()
    }
}
