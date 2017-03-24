package ru.merkulyevsasha.news.services;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class NewsJobService extends JobService {

    private static String TAG = NewsJobService.class.getSimpleName();

    @Override
    public boolean onStartJob(JobParameters params) {

        Log.d(TAG, "on start job");

        if (!ServicesHelper.isBatteryGood(this)){
            Log.d(TAG, "on start job: battery low");
            return false;
        }

        Log.d(TAG, "on start job: submit service thread");
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(new NewsRunnable(this.getApplicationContext()));

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {

        Log.d(TAG, "on stop job");
        return true;
    }
}
