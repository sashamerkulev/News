package ru.merkulyevsasha.news.newsjobs;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;

import java.util.concurrent.TimeUnit;


import ru.merkulyevsasha.news.R;
import ru.merkulyevsasha.news.domain.NewsInteractorImpl;
import ru.merkulyevsasha.news.presentation.main.MainActivity;


/**
 * Created by sasha_merkulev on 10.07.2017.
 */

public class NewsJob extends Job {

    private static final String TAG = NewsJob.class.getName();

    private final NewsInteractorImpl newsInteractor;

    NewsJob(NewsInteractorImpl newsInteractor) {
        this.newsInteractor = newsInteractor;
    }

    @SuppressLint("CheckResult")
    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        if (newsInteractor.needUpdate()) {
            newsInteractor.readNewsAndSaveToDb(R.id.nav_all)
                    .subscribe((articles, throwable) -> {

                    });
            sendNotification(getContext());
        }
        return Result.SUCCESS;
    }

    public static void scheduleJob() {
        new JobRequest.Builder(NewsJob.TAG)
                .setPeriodic(TimeUnit.MINUTES.toMillis(60))
                .setRequiresCharging(false)
                .setRequiresDeviceIdle(false)
                .setRequiredNetworkType(JobRequest.NetworkType.ANY)
                .build()
                .schedule();
    }

    private void sendNotification(Context context) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, "news_chanell_993")
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText("Есть свежие новости!")
                        .setAutoCancel(true);
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, MainActivity.class);
//        resultIntent.putExtra(KeysBundleHolder.KEY_ID, id);
//        resultIntent.putExtra(KeysBundleHolder.KEY_NAME, name);
//        resultIntent.putExtra(KeysBundleHolder.KEY_NOTIFICATION, true);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        if (notificationManager != null) notificationManager.notify(993, builder.build());
    }


}
