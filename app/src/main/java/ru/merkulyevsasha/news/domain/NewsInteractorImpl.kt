package ru.merkulyevsasha.news.domain

import io.reactivex.Flowable
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
            if (need) refreshArticles(navId)
            else throw NoNeedRefreshException()
        }
    }

    override fun refreshArticles(navId: Int): Single<List<Article>> {
        return newsRepository.getProgress()
            .flatMap { refreshing ->
                if (refreshing) throw AlreadyRefreshException()
                newsRepository.setProgress(true)
                newsRepository.readNewsAndSaveToDb(navId)
                    .doFinally {
                        newsRepository.setProgress(false)
                    }
            }
    }

    override fun readOrGetArticles(navId: Int): Single<List<Article>> {
        return readAllArticles()
            .flattenAsFlowable { t -> t }
            .switchIfEmpty(Flowable.defer { refreshArticles(navId).flattenAsFlowable { t -> t } })
            .toList()
    }
}
