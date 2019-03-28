package ru.merkulyevsasha.news.newsjobs

import android.os.Build
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NewsWorkerPeriodicRunner @Inject constructor() : BackgroundPeriodicWorker {

    override fun runWorker() {
        val constraints =
            Constraints.Builder()
                .setRequiresCharging(false)
                .setRequiredNetworkType(NetworkType.NOT_ROAMING)
                .setRequiresBatteryNotLow(true)
                .setRequiresStorageNotLow(false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            constraints
                .setRequiresDeviceIdle(false)
        }
        val worker = PeriodicWorkRequestBuilder<NewsWorker>(1, TimeUnit.HOURS)
            .setConstraints(constraints.build())
            .addTag(NewsWorker::class.java.simpleName)
            .build()
        WorkManager.getInstance().enqueue(worker)
    }
}