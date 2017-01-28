package ru.merkulyevsasha.news.loaders;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ru.merkulyevsasha.news.R;
import ru.merkulyevsasha.news.db.DatabaseHelper;
import ru.merkulyevsasha.news.models.ItemNews;

import static ru.merkulyevsasha.news.MainActivity.KEY_NAV_ID;
import static ru.merkulyevsasha.news.MainActivity.KEY_REFRESHING;

public class HttpService extends Service {

    public static final String ACTION_NAME="ru.merkulyevsasha.news.DATA_LOADING";
    public static final String KEY_FINISH_NAME="finished";
    public static final String KEY_UPDATE_NAME="updated";

    private final String TAG = "HttpService";

    private boolean isRunning = false;
    private long mLastDownloadDate;

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
        final boolean refreshing = intent.getBooleanExtra(KEY_REFRESHING, false);
        if (refreshing && !isRunning){
            sendBroadcast(true, true);
            return super.onStartCommand(intent, flags, startId);
        }

        if (!isRunning) {
            isRunning = true;
            mLastDownloadDate = new Date().getTime();

            new Thread(new Runnable() {
                public void run() {
                    try {
                        Log.d(TAG, "onStartCommand:startThread");

                        final int mNavId = intent.getIntExtra(KEY_NAV_ID, -1);
                        if (mNavId > 0) {
                            Log.d(TAG, String.valueOf(mNavId));

                            DatabaseHelper mHelper = DatabaseHelper.getInstance(DatabaseHelper.getDbPath(HttpService.this));
                            HttpReader reader = new HttpReader();

                            if (mNavId == R.id.nav_all){
                                for (Map.Entry<Integer, String> entry : reader.mConst.getLinks().entrySet()) {
                                    try {
                                        List<ItemNews> result = new ArrayList<ItemNews>();
                                        Integer key = entry.getKey();
                                        String url = entry.getValue();
                                        List<ItemNews> items = reader.GetHttpData(key, url);
                                        if (items != null) {
                                            result.addAll(items);
                                        }
                                        mHelper.delete(key);
                                        mHelper.addListNews(result);
                                        sendBroadcast(true, false);
                                    } catch(Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                List<ItemNews> result = reader.GetHttpData(mNavId, reader.mConst.getLinkByNavId(mNavId));
                                mHelper.delete(mNavId);
                                mHelper.addListNews(result);
                                sendBroadcast(true, false);
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
        return super.onStartCommand(intent, flags, startId);
    }

    public class HttpServiceBinder extends Binder {
        HttpService getService() {
            return HttpService.this;
        }
    }

}
