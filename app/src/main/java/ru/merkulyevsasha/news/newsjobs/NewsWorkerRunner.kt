package ru.merkulyevsasha.news.newsjobs

import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NewsWorkerRunner @Inject constructor() : BackgroundWorker {
    override fun run() {
        val worker = PeriodicWorkRequestBuilder<NewsWorker>(1, TimeUnit.HOURS).build()
        val status = WorkManager.getInstance().getStatusById(worker.id).get()
        if (status == null || status.state.isFinished) {
            WorkManager.getInstance().enqueue(worker)
        }
    }
}