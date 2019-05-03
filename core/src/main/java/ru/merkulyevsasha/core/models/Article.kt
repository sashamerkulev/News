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
    var usersLikeCount: Int,
    var usersDislikeCount: Int,
    var usersCommentCount: Int,
    var isUserLiked: Boolean,
    var isUserDisliked: Boolean,
    var isUserCommented: Boolean
)