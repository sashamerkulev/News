package ru.merkulyevsasha.core.preferences

import java.util.*

interface KeyValueStorage {
    fun getAccessToken(): String
    fun setAccessToken(token: String)
    fun getSetupId(): String
    fun setSetupId(setupId: String)
    fun getLastArticleReadDate(): Date?
    fun setLastArticleReadDate(lastDate: Date)
    fun saveNameAndPhone(name: String, phone: String)
    fun saveProfileFileName(profileFileName: String)
    fun getUserName(): String
    fun getUserPhone(): String
    fun getUserAvatarFileName(): String
}
