package ru.merkulyevsasha.core.preferences

import io.reactivex.Single

interface SharedPreferences {
    fun getAccessToken(): Single<String>
    fun setAccessToken(token: String)
    fun getSetupId(): Single<String>
    fun setSetupId(setupId: String)
}