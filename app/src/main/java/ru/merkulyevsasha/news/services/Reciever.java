package ru.merkulyevsasha.news.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class Reciever extends BroadcastReceiver {

    private static String TAG = Reciever.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action.equals(Intent.ACTION_BOOT_COMPLETED)
                || action.equals("android.intent.action.QUICKBOOT_POWERON")
                || action.equals("com.htc.intent.action.QUICKBOOT_POWERON") ) {

            Log.d(TAG, "on receive: register after reboot");
            ServicesHelper.register(context);
        } else {
            Log.d(TAG, "on receive: start service");
            context.startService(new Intent(context, NewsService.class));
        }

    }
}
