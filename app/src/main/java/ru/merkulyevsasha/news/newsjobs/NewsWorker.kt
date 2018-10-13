package ru.merkulyevsasha.news.newsjobs

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class NewsWorker constructor(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
