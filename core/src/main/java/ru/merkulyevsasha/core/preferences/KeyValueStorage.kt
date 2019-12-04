package ru.merkulyevsasha.core.preferences

import ru.merkulyevsasha.core.models.ThemeEnum
import java.util.*

interface KeyValueStorage {
    fun getAccessToken(): String
    fun setAccessToken(token: String)
    fun getSetupId(): String
    fun setSetupId(setupId: String)
    fun getLastArticleReadDate(): Date?
    fun setLastArticleReadDate(lastDate: Date)
    fun getLastArticleCommentReadDate(): Date?
    fun setLastArticleCommentReadDate(lastDate: Date)
    fun saveNameAndPhone(name: String, phone: String)
    fun saveProfileFileName(profileFileName: String)
    fun getUserName(): String
    fun getUserPhone(): String
    fun getUserAvatarFileName(): String
    fun isApplicationAlreadyRatedFlag(): Boolean
    fun setApplicationRatedFlag()
    fun getApplicationRunNumber(): Int
    fun updateApplicationRunNumber()
    fun getLastApplicationRunDate(): Long
    fun updateLastApplicationRunDate()
    fun getUserProfileTheme(): ThemeEnum
    fun setUserProfileTheme(theme: ThemeEnum)
}
