package ru.merkulyevsasha.news.data.prefs

import android.content.Context
import android.content.SharedPreferences

import javax.inject.Inject

import io.reactivex.Single

/**
 * Created by sasha_merkulev on 21.09.2017.
 */

class NewsSharedPreferencesImpl
@Inject constructor(context: Context) : NewsSharedPreferences {

    private val prefs: SharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)

    override val firstRunFlag: Single<Boolean>
        get() = Single.fromCallable { prefs.getBoolean(KEY_FIRST_RUN, true) }

    override fun setFirstRunFlag() {
        val editor = prefs.edit()
        editor.putBoolean(KEY_FIRST_RUN, false)
        editor.apply()
    }

    companion object {
        private val SHARED_PREF_NAME = "news_pref"
        private val KEY_FIRST_RUN = "ru.merkulyevsasha.news.prefs_key_run_flag"
    }

}
