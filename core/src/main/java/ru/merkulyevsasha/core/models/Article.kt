package ru.merkulyevsasha.core.models

import java.util.*

class Article(
    val articleId: Int,
    val sourceName: String,
    val title: String,
    val link: String,
    val description: String?,
    val pubDate: Date,
    val category: String,
    val pictureUrl: String?,
    val usersLikeCount: Int,
    val usersDislikeCount: Int,
    val isUserLiked: Boolean,
    val isUserDisliked: Boolean,
    val isUserCommented: Boolean
)