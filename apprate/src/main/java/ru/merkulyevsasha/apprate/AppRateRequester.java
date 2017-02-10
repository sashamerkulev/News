package ru.merkulyevsasha.apprate;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;

import java.util.Calendar;


public class AppRateRequester {

    private final static int MAX_COUNT = 10;
    private final static int MAX_DAYS_WAIT = 3;

    public static void Run(final Context context, final String packagename){

        final PreferencesHelper sharedPreferences = new PreferencesHelper(context);

        if (!sharedPreferences.isRated())
        {

            int count = sharedPreferences.getCount();
            long date = sharedPreferences.getDate();
            sharedPreferences.updateCountRuns();

            if (count > MAX_COUNT ) {

                if (date > 0){
                    Calendar calendar = Calendar.getInstance();
                    long now = calendar.getTimeInMillis();

                    long days =  (now - date) / (24 * 60 * 60 * 1000);
                    if (days < MAX_DAYS_WAIT)
                        return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.rate_title);
                builder.setMessage(R.string.rate_message);
                builder.setCancelable(false);

                builder.setNeutralButton(R.string.remind_later_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sharedPreferences.updateDate();
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton(R.string.no_thanks_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sharedPreferences.setRated();
                        dialog.dismiss();
                    }
                });

                builder.setPositiveButton(R.string.rate_now_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sharedPreferences.setRated();
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+packagename)));
                        dialog.dismiss();
                    }
                });

                builder.show();
            }
        }

    }

}
