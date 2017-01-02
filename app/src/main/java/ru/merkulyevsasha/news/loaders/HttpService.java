package ru.merkulyevsasha.news.loaders;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

import ru.merkulyevsasha.news.MainActivity;

import static ru.merkulyevsasha.news.MainActivity.KEY_INTENT;
import static ru.merkulyevsasha.news.MainActivity.KEY_NAV_ID;

public class HttpService extends Service {

    private final String TAG = "HttpService";

    private boolean isRunning = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {

        Log.d(TAG, "onStartCommand");
        final PendingIntent pendingIntent = intent.getParcelableExtra(KEY_INTENT);

        if (!isRunning) {
            isRunning = true;

            new Thread(new Runnable() {
                public void run() {
                    try {
                        Log.d(TAG, "onStartCommand:startThread");

                        final int navId = intent.getIntExtra(KEY_NAV_ID, -1);
                        if (navId > 0) {
                            Log.d(TAG, String.valueOf(navId));

                            HttpReader reader = new HttpReader(HttpService.this, navId);
                            reader.load();

                            pendingIntent.send(HttpService.this, MainActivity.STATUS_FINISH, intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        FirebaseCrash.report(e);
                    } finally {
                        stopSelf(startId);
                    }
                }
            }).start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

}
