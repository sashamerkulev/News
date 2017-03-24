package ru.merkulyevsasha.news.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

import java.util.List;
import java.util.Map;

import ru.merkulyevsasha.news.R;
import ru.merkulyevsasha.news.data.db.DatabaseHelper;
import ru.merkulyevsasha.news.data.http.HttpReader;
import ru.merkulyevsasha.news.helpers.Const;
import ru.merkulyevsasha.news.pojos.ItemNews;

import static ru.merkulyevsasha.news.presentation.MainActivity.KEY_NAV_ID;
import static ru.merkulyevsasha.news.presentation.MainActivity.KEY_REFRESHING;

public class HttpService extends Service {

    public static final String ACTION_NAME="ru.merkulyevsasha.news.DATA_LOADING";
    public static final String KEY_FINISH_NAME="finished";
    public static final String KEY_UPDATE_NAME="updated";

    private final String TAG = "HttpService";

    private boolean isRunning = false;

    private DatabaseHelper mHelper;
    private HttpReader reader;


    @Override
    public IBinder onBind(Intent intent) {
        return new HttpServiceBinder();
    }

    private void sendBroadcast(boolean updated, boolean finished){
        Intent intent = new Intent(ACTION_NAME);
        intent.putExtra(KEY_UPDATE_NAME, updated);
        intent.putExtra(KEY_FINISH_NAME, finished);
        sendBroadcast(intent);
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {

        Log.d(TAG, "onStartCommand");
        if (intent == null)
            return START_NOT_STICKY;

        final boolean refreshing = intent.getBooleanExtra(KEY_REFRESHING, false);
        if (refreshing && !isRunning){
            sendBroadcast(true, true);
            stopSelf(startId);
            return START_NOT_STICKY;
        }

        if (!isRunning) {
            isRunning = true;

            mHelper = DatabaseHelper.getInstance(DatabaseHelper.getDbPath(HttpService.this));
            reader = new HttpReader();

            new Thread(new Runnable() {
                public void run() {
                    try {
                        Log.d(TAG, "onStartCommand:startThread");

                        final int mNavId = intent.getIntExtra(KEY_NAV_ID, -1);
                        if (mNavId > 0) {
                            Log.d(TAG, String.valueOf(mNavId));

                            final Const mConst = new Const();
                            if (mNavId == R.id.nav_all){
                                for (Map.Entry<Integer, String> entry : mConst.getLinks().entrySet()) {
                                    readHttpDataAndSaveToDb(entry.getKey(), entry.getValue());
                                }
                            } else {
                                readHttpDataAndSaveToDb(mNavId, mConst.getLinkByNavId(mNavId));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        FirebaseCrash.report(e);
                    } finally {
                        sendBroadcast(false, true);
                        isRunning = false;
                        stopSelf(startId);
                    }
                }
            }).start();
        }
        return START_NOT_STICKY;
    }

    private void readHttpDataAndSaveToDb(int id, String url){
        try {
            List<ItemNews> items = reader.GetHttpData(id, url);
            if (items.size() > 0) {
                mHelper.delete(id);
                mHelper.addListNews(items);
                sendBroadcast(true, false);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public class HttpServiceBinder extends Binder {
        public HttpService getService() {
            return HttpService.this;
        }
    }

}
