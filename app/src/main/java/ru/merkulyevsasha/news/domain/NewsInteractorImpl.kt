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

    override fun readAllArticles(): Single<List<Article>> {
        return newsRepository.readAllArticles()
    }

    override fun readArticlesByNavId(navId: Int): Single<List<Article>> {
        return newsRepository.readArticlesByNavId(navId)
    }

    override fun search(searchTtext: String): Single<List<Article>> {
        return newsRepository.search("%$searchTtext%")
    }

    override fun refreshArticlesIfNeed(navId: Int): Single<List<Article>> {
        val needUpdate = Single.fromCallable {
            val lastpubDate = newsRepository.getLastPubDate()
            if (lastpubDate == null) true
            else {
                val nowdate = Date()
                val diffMinutes = (nowdate.time / 60000 - lastpubDate.time / 60000)
                diffMinutes > 30
            }
        }
        return needUpdate.flatMap { need ->
            if (need) refreshArticles(navId, null)
            else throw NoNeedRefreshException()
        }
    }

    override fun refreshArticles(navId: Int, searchText: String?): Single<List<Article>> {
        return newsRepository.getProgress()
            .flatMap { refreshing ->
                if (refreshing) throw AlreadyRefreshException()
                newsRepository.setProgress(true)
                newsRepository.readNewsAndSaveToDb(navId)
                    .flatMap { items ->
                        if (searchText.isNullOrEmpty()) Single.just(items)
                        else search(searchText!!)
                    }
                    .doFinally {
                        newsRepository.setProgress(false)
                    }
            }
    }
}
