package ru.merkulyevsasha.news.newsjobs

import android.os.Build
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NewsWorkerRunner @Inject constructor() : BackgroundWorker {
    override fun run() {
        val constraints =
            Constraints.Builder()
                .setRequiresCharging(false)
                .setRequiredNetworkType(NetworkType.NOT_ROAMING)
                .setRequiresBatteryNotLow(true)
                .setRequiresStorageNotLow(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            constraints
                .setRequiresDeviceIdle(false)
        }
        val worker = PeriodicWorkRequestBuilder<NewsWorker>(1, TimeUnit.HOURS)
            .setConstraints(constraints.build())
            .build()
        WorkManager.getInstance().enqueue(worker)
    }
}