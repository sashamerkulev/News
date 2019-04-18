package ru.merkulyevsasha.core.preferences

interface KeyValueStorage {
    fun getAccessToken(): String
    fun setAccessToken(token: String)
    fun getSetupId(): String
    fun setSetupId(setupId: String)
}