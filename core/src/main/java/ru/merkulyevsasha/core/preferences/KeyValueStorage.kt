package ru.merkulyevsasha.core.preferences

import java.util.Date

interface KeyValueStorage {
    fun getAccessToken(): String
    fun setAccessToken(token: String)
    fun getSetupId(): String
    fun setSetupId(setupId: String)
    fun getLastArticleReadDate(): Date?
    fun setLastArticleReadDate(lastDate: Date)
}