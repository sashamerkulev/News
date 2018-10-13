package ru.merkulyevsasha.news.data.prefs

import io.reactivex.Single

interface NewsSharedPreferences {
    fun getFirstRun(): Single<Boolean>
    fun setFirstRun()
    fun getProgress(): Single<Boolean>
    fun setProgress(progress: Boolean)
}
