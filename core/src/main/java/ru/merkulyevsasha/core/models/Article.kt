package ru.merkulyevsasha.core.models

import java.util.*

class Article(
    val articleId: Int,
    val sourceName: String,
    val title: String,
    val description: String?,
    val pubDate: Date,
    val category: String,
    val pictureUrl: String?,
    val likes: Long,
    val dislikes: Long,
    val like: Boolean,
    val dislike: Boolean
    )