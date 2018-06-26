package ru.merkulyevsasha.news.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;

import io.reactivex.Single;

/**
 * Created by sasha_merkulev on 21.09.2017.
 */

public class NewsSharedPreferencesImpl implements NewsSharedPreferences {

    private final static String SHARED_PREF_NAME = "news_pref";

    private final static String KEY_FIRST_RUN = "ru.merkulyevsasha.news.prefs_key_run_flag";

    private final SharedPreferences prefs;

    @Inject
    public NewsSharedPreferencesImpl(Context context){
        prefs = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public Single<Boolean> getFirstRunFlag() {
        return Single.fromCallable(() -> prefs.getBoolean(KEY_FIRST_RUN, true));
    }

    @Override
    public void setFirstRunFlag() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_FIRST_RUN, false);
        editor.apply();
    }

}
