package ru.merkulyevsasha.news.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class NewsService extends Service {

    private static String TAG = NewsService.class.getSimpleName();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "on start command");
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()){
            Log.d(TAG, "on start command: wifi is disabled");
            ServicesHelper.registerAlarmNewsService(this);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        if (!ServicesHelper.isBatteryGood(this)){
            Log.d(TAG, "on start command: battery low");
            ServicesHelper.registerAlarmNewsService(this);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        Log.d(TAG, "on start command: submit service thread");
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(new NewsRunnable(this.getApplicationContext()));
        Log.d(TAG, "on start command: register alarm");
        ServicesHelper.registerAlarmNewsService(this);

        return super.onStartCommand(intent, flags, startId);
    }
}
