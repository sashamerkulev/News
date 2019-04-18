package ru.merkulyevsasha.database.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import java.util.*

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
    val search: String
)