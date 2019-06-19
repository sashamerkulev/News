package ru.merkulyevsasha.core.models

import java.util.*

data class ArticleComment(
    val articleId: Int,
    val commentId: Int,
    val userId: Int,
    val userName: String,
    val pubDate: Date,
    val lastActivityDate: Date,
    val comment: String,
    val statusId: Int,
    var usersLikeCount: Int,
    var usersDislikeCount: Int,
    var isUserLiked: Boolean,
    var isUserDisliked: Boolean,
    val owner: Boolean,
    val avatarUrl: String,
    val authorization: String
) : ArticleOrComment
