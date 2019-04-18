package ru.merkulyevsasha.database.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity(tableName = "comments", indices = [Index("articleId")])
class ArticleCommentsEntity(
    @PrimaryKey
    val commentId: Int,
    val articleId: Int,
    val userName: String,
    val userPhoto: String,
    val date: Date,
    val comment: String,
    val status: Int
)