package ru.merkulyevsasha.database.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import java.util.Date

@Entity(tableName = "articles", indices = [Index("sourceName"), Index("search")])
class ArticleEntity(
    @PrimaryKey
    val articleId: Int = 0,
    val sourceName: String,
    val title: String,
    val link: String,
    val description: String,
    val pubDate: Date,
    val category: String,
    val pictureUrl: String,
    val usersLikeCount: Int,
    val usersDislikeCount: Int,
    val usersCommentCount: Int,
    val isUserLiked: Boolean,
    val isUserDisliked: Boolean,
    val isUserCommented: Boolean,
    val search: String
)