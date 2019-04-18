package ru.merkulyevsasha.preferences

import android.content.Context
import ru.merkulyevsasha.core.preferences.KeyValueStorage

class KeyValueStorageImpl(context: Context) : KeyValueStorage {

    private val prefs: android.content.SharedPreferences = context.getSharedPreferences("keyvalue", Context.MODE_PRIVATE)

    private var token: String = ""
    private var setupId: String = ""

    override fun getAccessToken(): String {
        return token
    }

    override fun setAccessToken(token: String) {
        this.token = token
        prefs.edit().putString("TOKEN", token).apply()
    }

    override fun getSetupId(): String {
        return setupId
    }

    override fun setSetupId(setupId: String) {
        this.setupId = setupId
        prefs.edit().putString("SETUP_ID", setupId).apply()
    }

}