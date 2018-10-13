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
constructor(
    private val newsConstants: NewsConstants,
    private val reader: HttpReader,
    private val prefs: NewsSharedPreferences,
    private val newsDbRepository: NewsDbRepository,
    private val broadcastHelper: BroadcastHelper
) : NewsInteractor {

    override fun getFirstRun(): Single<Boolean> {
        return prefs
                .getFirstRun()
                .subscribeOn(Schedulers.io())
    }

    override fun setFirstRunFlag() {
        prefs.setFirstRun()
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
            broadcastHelper.sendFinishBroadcast()

            items
        }
                .flattenAsFlowable { t -> t }
                .sorted { article, t1 -> t1.pubDate.compareTo(article.pubDate) }
                .toList()
                .cache()
                .subscribeOn(Schedulers.io())
    }

    override fun selectAll(): Single<List<Article>> {
        return newsDbRepository.selectAll()
                .subscribeOn(Schedulers.io())
    }

    override fun selectNavId(navId: Int): Single<List<Article>> {
        return newsDbRepository.selectNavId(navId)
                .subscribeOn(Schedulers.io())
    }

    override fun search(searchTtext: String): Single<List<Article>> {
        return newsDbRepository.search(searchTtext)
                .subscribeOn(Schedulers.io())
    }

    override fun needUpdate(): Boolean {
        val lastpubDate = newsDbRepository.getLastPubDate() ?: return true
        val nowdate = Date()
        val diffMinutes = (nowdate.time / 60000 - lastpubDate.time / 60000)
        return diffMinutes > 30
    }

    private fun getArticles(id: Int, url: String): List<Article> {
        var items: List<Article> = ArrayList()
        try {
            items = reader.getHttpData(id, url)
            if (items.isNotEmpty()) {
                newsDbRepository.delete(id)
                newsDbRepository.addListNews(items)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return items
    }

    private fun updaDbAndSendNotification(articles: List<Article>, key: Int) {
        newsDbRepository.delete(key)
        newsDbRepository.addListNews(articles)
        broadcastHelper.sendUpdateBroadcast()
    }
}
