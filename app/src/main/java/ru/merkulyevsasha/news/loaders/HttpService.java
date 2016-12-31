package ru.merkulyevsasha.news.loaders;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import ru.merkulyevsasha.news.MainActivity;

import static ru.merkulyevsasha.news.MainActivity.KEY_INTENT;
import static ru.merkulyevsasha.news.MainActivity.KEY_NAV_ID;

public class HttpService extends Service {

    private final String TAG = "HttpService";

    private PendingIntent pendingIntent;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {

        new Thread(new Runnable() {
            public void run() {
                Log.d(TAG, "onStartCommand");
                try {
                    pendingIntent = intent.getParcelableExtra(KEY_INTENT);

                    final int navId = intent.getIntExtra(KEY_NAV_ID, -1);
                    if (navId > 0){
                        Log.d(TAG, String.valueOf(navId));

                        HttpReader reader = new HttpReader(HttpService.this, navId);
                        reader.load();

                        pendingIntent.send(HttpService.this, MainActivity.STATUS_FINISH, intent);
                        stopSelf(startId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

}
