package ru.merkulyevsasha.news.helpers;

import android.content.Context;
import android.content.Intent;

/**
 * Created by sasha_merkulev on 22.09.2017.
 */

public class BroadcastHelper {

    public static final String ACTION_NAME="ru.merkulyevsasha.news.DATA_LOADING";
    public static final String KEY_FINISH_NAME="ru.merkulyevsasha.news.key_finished";
    public static final String KEY_UPDATE_NAME="ru.merkulyevsasha.news.key_updated";

    private final Context context;

    public BroadcastHelper(Context context){
        this.context = context;
    }

    private void sendBroadcast(boolean updated, boolean finished){
        Intent intent = new Intent(ACTION_NAME);
        intent.putExtra(KEY_UPDATE_NAME, updated);
        intent.putExtra(KEY_FINISH_NAME, finished);
        context.sendBroadcast(intent);
    }

    public void sendFinishBroadcast(){
        sendBroadcast(false, true);
    }

    public void sendUpdateBroadcast(){
        sendBroadcast(true, false);
    }
}
