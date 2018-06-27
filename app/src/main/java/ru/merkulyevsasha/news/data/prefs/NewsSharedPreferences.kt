package ru.merkulyevsasha.news.data.prefs

import io.reactivex.Single

interface NewsSharedPreferences {
    val firstRunFlag: Single<Boolean>

    fun setFirstRunFlag()
}
