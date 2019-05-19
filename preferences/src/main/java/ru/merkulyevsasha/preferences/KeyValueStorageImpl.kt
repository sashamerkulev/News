package ru.merkulyevsasha.preferences

import android.content.Context
import ru.merkulyevsasha.core.preferences.KeyValueStorage
import java.util.*

class KeyValueStorageImpl(context: Context) : KeyValueStorage {

    private val prefs: android.content.SharedPreferences =
        context.getSharedPreferences("keyvalue", Context.MODE_PRIVATE)

    private var token: String = ""
    private var setupId: String = ""
    private var lastArticleReadDate: Date? = null
    private var fileName: String = ""
    private var name: String = ""
    private var phone: String = ""

    init {
        token = prefs.getString("TOKEN", "") ?: ""
        setupId = prefs.getString("SETUP_ID", "") ?: ""
        lastArticleReadDate = Date(prefs.getLong("LAST_ARTICLE_READ", 0))
        fileName = prefs.getString("FILE_NAME", "") ?: ""
        name = prefs.getString("NAME", "") ?: ""
        phone = prefs.getString("PHONE", "") ?: ""
    }

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

    override fun getLastArticleReadDate(): Date? {
        return lastArticleReadDate
    }

    override fun setLastArticleReadDate(lastDate: Date) {
        lastArticleReadDate = lastDate
        prefs.edit().putLong("LAST_ARTICLE_READ", lastDate.time).apply()
    }

    override fun saveNameAndPhone(name: String, phone: String) {
        this.name = name
        this.phone = phone
        prefs
            .edit()
            .putString("NAME", name)
            .putString("PHONE", phone)
            .apply()
    }

    override fun saveProfileFileName(profileFileName: String) {
        this.fileName = profileFileName
        prefs
            .edit()
            .putString("FILE_NAME", profileFileName)
            .apply()
    }

    override fun getUserName(): String {
        return name
    }

    override fun getUserPhone(): String {
        return phone
    }

    override fun getUserAvatarFileName(): String {
        return fileName
    }

}
