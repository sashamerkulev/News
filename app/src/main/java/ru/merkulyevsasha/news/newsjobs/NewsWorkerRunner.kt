package ru.merkulyevsasha.news.newsjobs

import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import javax.inject.Inject

class NewsWorkerRunner @Inject constructor() : BackgroundWorker {

    override fun runWorker(navId: Int, searchText: String?) {

        val data: Data = Data.Builder()
            .putBoolean("singleRun", true)
            .putInt("navId", navId)
            .putString("searchText", searchText)
            .build()

        val worker = OneTimeWorkRequestBuilder<NewsWorker>()
            .setInputData(data)
            .build()
        WorkManager.getInstance().enqueue(worker)
    }
}