package ru.merkulyevsasha.news.domain

import io.reactivex.Single
import ru.merkulyevsasha.news.R
import ru.merkulyevsasha.news.data.NewsRepository
import ru.merkulyevsasha.news.models.Article
import ru.merkulyevsasha.news.newsjobs.BackgroundPeriodicWorker
import ru.merkulyevsasha.news.newsjobs.BackgroundWorker
import javax.inject.Inject

class NewsInteractorImpl @Inject
constructor(
    private val newsRepository: NewsRepository,
    private val periodicWorker: BackgroundPeriodicWorker,
    private val worker: BackgroundWorker
) : NewsInteractor {

    override fun getProgress(): Single<Boolean> {
        return newsRepository.getProgress()
    }

    override fun setProgress(progress: Boolean) {
        newsRepository.setProgress(progress)
    }

    override fun readArticlesByNavId(navId: Int, searchText: String?): Single<List<Article>> {
        if (searchText != null && searchText.isNotEmpty()) return search(searchText)
        return when (navId) {
            R.id.nav_all -> readAllArticles()
            else -> readArticlesByNavId(navId)
        }
    }

    override fun search(searchTtext: String): Single<List<Article>> {
        return newsRepository.search("%$searchTtext%")
    }

    override fun startRefreshWorker(navId: Int, searchText: String?) {
        worker.runWorker(navId, searchText)
    }

    override fun startRefreshWorker() {
        periodicWorker.runWorker()
    }

    override fun refreshArticles(navId: Int): Single<List<Article>> {
        return newsRepository.readNewsAndSaveToDb(navId)
    }

    private fun readAllArticles(): Single<List<Article>> {
        return newsRepository.readAllArticles()
    }
}
