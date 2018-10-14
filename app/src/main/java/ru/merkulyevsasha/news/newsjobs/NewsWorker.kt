package ru.merkulyevsasha.news.newsjobs

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.TaskStackBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import ru.merkulyevsasha.news.R
import ru.merkulyevsasha.news.dagger.modules.AppProvidesModule
import ru.merkulyevsasha.news.data.NewsRepositoryImpl
import ru.merkulyevsasha.news.data.db.DbDataSourceImpl
import ru.merkulyevsasha.news.data.http.HttpDataSourceImpl
import ru.merkulyevsasha.news.data.prefs.NewsSharedPreferencesImpl
import ru.merkulyevsasha.news.data.utils.NewsConstants
import ru.merkulyevsasha.news.domain.NewsInteractor
import ru.merkulyevsasha.news.domain.NewsInteractorImpl
import ru.merkulyevsasha.news.presentation.main.MainActivity
import java.util.concurrent.locks.ReentrantLock

class NewsWorker constructor(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    private val reentrantLock = ReentrantLock()
    private val app = AppProvidesModule()
    private val newsInteractor: NewsInteractor = NewsInteractorImpl(
        NewsRepositoryImpl(
            HttpDataSourceImpl(app.providesOkHttpClient()),
            DbDataSourceImpl(app.providesNewsDbRoom(applicationContext)),
            NewsSharedPreferencesImpl(applicationContext),
            NewsConstants(applicationContext)))

    override fun doWork(): Result {
        if (reentrantLock.tryLock()) {
            try {
                println("NewsWorker.doWork: start")
                val items = newsInteractor
                    .refreshArticlesIfNeed(R.id.nav_all)
                    .blockingGet()
                println("NewsWorker.doWork: finish: ${items.size}")
                sendNotification(applicationContext)
            } catch (e: Exception) {
                println("NewsWorker.doWork: finish: error $e")
            } finally {
                println("NewsWorker.doWork: finish")
                reentrantLock.unlock()
            }
        }
        return Result.SUCCESS
    }

    // TODO need to move in other class
    private fun sendNotification(context: Context) {
        val builder = NotificationCompat.Builder(context, context.getString(R.string.notification_channell_id))
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
        notificationManager.notify(993, builder.build())
    }

}
