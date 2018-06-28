package ru.merkulyevsasha.news.newsjobs

import com.evernote.android.job.Job
import com.evernote.android.job.JobCreator

import javax.inject.Inject

import ru.merkulyevsasha.news.domain.NewsInteractorImpl

class NewsJobCreator @Inject
internal constructor(private val newsInteractor: NewsInteractorImpl) : JobCreator {

    override fun create(tag: String): Job? {
        return NewsJob(newsInteractor)
    }
}
