package ru.merkulyevsasha.apprate;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;

class PreferencesHelper {

    private static final String APP_PREFERENCES = "appraterequester";
    private static final String KEY_COUNT = "count";
    private static final String KEY_RATED = "rated";
    private static final String KEY_DATE = "last_date";

    private final SharedPreferences sharedPreferences;

    PreferencesHelper(Context context){
        sharedPreferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    void updateCountRuns(){
        int count = getCount();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_COUNT, count + 1);

        editor.apply();
    }

    void updateDate(){

        SharedPreferences.Editor editor = sharedPreferences.edit();

        Calendar calendar = Calendar.getInstance();

        editor.putLong(KEY_DATE, calendar.getTimeInMillis());

        editor.apply();
    }

    int getCount(){
        return sharedPreferences.getInt(KEY_COUNT, 0);
    }

    long getDate(){
        return sharedPreferences.getLong(KEY_DATE, 0);
    }

    boolean isRated(){
        return sharedPreferences.getBoolean(KEY_RATED, false);
    }

    void setRated() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(KEY_RATED, true);

        editor.apply();
    }

}
