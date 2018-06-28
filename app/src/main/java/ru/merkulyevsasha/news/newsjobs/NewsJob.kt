package ru.merkulyevsasha.news.newsjobs

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.TaskStackBuilder

import com.evernote.android.job.Job
import com.evernote.android.job.JobRequest

import java.util.concurrent.TimeUnit


import ru.merkulyevsasha.news.R
import ru.merkulyevsasha.news.domain.NewsInteractorImpl
import ru.merkulyevsasha.news.presentation.main.MainActivity


/**
 * Created by sasha_merkulev on 10.07.2017.
 */

class NewsJob internal constructor(private val newsInteractor: NewsInteractorImpl) : Job() {

    @SuppressLint("CheckResult")
    override fun onRunJob(params: Job.Params): Job.Result {
        if (newsInteractor.needUpdate()) {
            newsInteractor.readNewsAndSaveToDb(R.id.nav_all)
                    .subscribe { articles, throwable -> sendNotification(context) }
        }
        return Job.Result.SUCCESS
    }

    private fun sendNotification(context: Context) {
        val builder = NotificationCompat.Builder(context, getContext().getString(R.string.notification_channell_id))
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText("Есть свежие новости!")
                .setAutoCancel(true)
        // Creates an explicit intent for an Activity in your app
        val resultIntent = Intent(context, MainActivity::class.java)
        //        resultIntent.putExtra(KeysBundleHolder.KEY_ID, id);
        //        resultIntent.putExtra(KeysBundleHolder.KEY_NAME, name);
        //        resultIntent.putExtra(KeysBundleHolder.KEY_NOTIFICATION, true);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        val stackBuilder = TaskStackBuilder.create(context)
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity::class.java)
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent)
        val resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(resultPendingIntent)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // mId allows you to update the notification later on.
        notificationManager?.notify(993, builder.build())
    }

    companion object {

        private val TAG = NewsJob::class.java.name

        fun scheduleJob() {
            JobRequest.Builder(NewsJob.TAG)
                    .setPeriodic(TimeUnit.MINUTES.toMillis(60))
                    .setRequiresCharging(false)
                    .setRequiresDeviceIdle(false)
                    .setRequiredNetworkType(JobRequest.NetworkType.ANY)
                    .build()
                    .schedule()
        }
    }


}
