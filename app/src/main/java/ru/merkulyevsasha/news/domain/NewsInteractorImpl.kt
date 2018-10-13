package ru.merkulyevsasha.news.domain

import io.reactivex.Single
import ru.merkulyevsasha.news.data.NewsRepository
import ru.merkulyevsasha.news.models.Article
import java.util.*
import javax.inject.Inject

class NewsInteractorImpl @Inject
constructor(
    private val newsRepository: NewsRepository
) : NewsInteractor {

    override fun getFirstRun(): Single<Boolean> {
        return newsRepository
            .getFirstRun()
    }

    override fun setFirstRunFlag() {
        newsRepository.setFirstRun()
    }

    override fun readNewsAndSaveToDb(navId: Int): Single<List<Article>> {
        return newsRepository.readNewsAndSaveToDb(navId)
    }

    override fun selectAll(): Single<List<Article>> {
        return newsRepository.selectAll()
    }

    override fun selectNavId(navId: Int): Single<List<Article>> {
        return newsRepository.selectNavId(navId)
    }

    override fun search(searchTtext: String): Single<List<Article>> {
        return newsRepository.search(searchTtext)
    }

    override fun needUpdate(): Boolean {
        val lastpubDate = newsRepository.getLastPubDate() ?: return true
        val nowdate = Date()
        val diffMinutes = (nowdate.time / 60000 - lastpubDate.time / 60000)
        return diffMinutes > 30
    }
}
