package ru.merkulyevsasha.preferences

import android.content.Context
import io.reactivex.Single
import ru.merkulyevsasha.core.preferences.SharedPreferences

class SharedPreferencesImpl(context: Context) : SharedPreferences {

    private val prefs: android.content.SharedPreferences = context.getSharedPreferences("keyvalue", Context.MODE_PRIVATE)

    override fun getAccessToken(): Single<String> {
        return Single.fromCallable { prefs.getString("TOKEN", "") ?: "" }
    }

    override fun setAccessToken(token: String) {
        prefs.edit().putString("TOKEN", token).apply()
    }

    override fun getSetupId(): Single<String> {
        return Single.fromCallable { prefs.getString("SETUP_ID", "") ?: "" }
    }

    override fun setSetupId(setupId: String) {
        prefs.edit().putString("SETUP_ID", setupId).apply()
    }

}