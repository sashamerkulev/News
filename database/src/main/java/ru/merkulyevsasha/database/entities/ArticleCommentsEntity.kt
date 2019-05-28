package ru.merkulyevsasha.database.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity(tableName = "comments",
    foreignKeys = [ForeignKey(entity = ArticleEntity::class, parentColumns = ["articleId"], childColumns = ["articleId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("articleId"), Index("pubDate")])
class ArticleCommentsEntity(
    @PrimaryKey
    val commentId: Int,
    val articleId: Int,
    val userId: Int,
    val userName: String,
    val pubDate: Date,
    val comment: String,
    val status: Int,
    val likes: Int,
    val dislikes: Int,
    val like: Boolean,
    val dislike: Boolean,
    val owner: Boolean
)