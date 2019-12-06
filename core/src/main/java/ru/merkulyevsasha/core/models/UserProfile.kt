package ru.merkulyevsasha.core.models

data class UserProfile(
    val userInfo: UserInfo,
    val theme: ThemeEnum,
    val sources: List<RssSource>
)
