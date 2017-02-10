package ru.merkulyevsasha.apprate;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;

public class PreferencesHelper {

    private static final String APP_PREFERENCES = "appraterequester";
    private static final String KEY_COUNT = "count";
    private static final String KEY_RATED = "rated";
    private static final String KEY_DATE = "last_date";

    private final SharedPreferences mSharedPreferences;

    public PreferencesHelper(Context context){
        mSharedPreferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void updateCountRuns(){
        int count = getCount();

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(KEY_COUNT, count + 1);

        editor.apply();
    }

    public void updateDate(){

        SharedPreferences.Editor editor = mSharedPreferences.edit();

        Calendar calendar = Calendar.getInstance();

        editor.putLong(KEY_DATE, calendar.getTimeInMillis());

        editor.apply();
    }

    public int getCount(){
        return mSharedPreferences.getInt(KEY_COUNT, 0);
    }

    public long getDate(){
        return mSharedPreferences.getLong(KEY_DATE, 0);
    }

    public boolean isRated(){
        return mSharedPreferences.getBoolean(KEY_RATED, false);
    }

    public void setRated() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        editor.putBoolean(KEY_RATED, true);

        editor.apply();
    }

}
