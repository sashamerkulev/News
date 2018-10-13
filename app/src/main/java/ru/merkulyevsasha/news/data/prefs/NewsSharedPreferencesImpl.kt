package ru.merkulyevsasha.news.data.prefs

import android.content.Context
import android.content.SharedPreferences

import javax.inject.Inject

import io.reactivex.Single

class NewsSharedPreferencesImpl
@Inject constructor(context: Context) : NewsSharedPreferences {

    private val prefs: SharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)

    override fun getFirstRun(): Single<Boolean> {
        return Single.fromCallable { prefs.getBoolean(KEY_FIRST_RUN, true) }
    }

    override fun setFirstRun() {
        prefs.edit()
            .putBoolean(KEY_FIRST_RUN, false)
            .apply()
    }

    override fun getProgress(): Single<Boolean> {
        return Single.fromCallable { prefs.getBoolean(KEY_PROGRESS, false) }
    }

    override fun setProgress(progress: Boolean) {
        prefs.edit()
            .putBoolean(KEY_PROGRESS, progress)
            .apply()
    }

    companion object {
        private const val SHARED_PREF_NAME = "news_pref"
        private const val KEY_FIRST_RUN = "prefs_key_run_flag"
        private const val KEY_PROGRESS = "prefs_key_progress_flag"
    }

}
