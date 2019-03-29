package ru.merkulyevsasha.preferences

import android.content.Context
import ru.merkulyevsasha.core.preferences.SharedPreferences

class SharedPreferencesImpl(context: Context) : SharedPreferences {

    private val prefs: android.content.SharedPreferences = context.getSharedPreferences("keyvalue", Context.MODE_PRIVATE)

    override fun getAccessToken(): String {
        return prefs.getString("TOKEN", "") ?: ""
    }
}