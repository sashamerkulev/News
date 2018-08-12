package ru.merkulyevsasha.news.data.db.entities


import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

import java.util.Date

@Entity(tableName = "articles", indices = [Index("sourceNavId"), Index("search")])
class ArticleEntity (
    val sourceNavId: Int,
    val title: String,
    val link: String,
    val description: String,
    val pubDate: Date,
    val category: String,
    val search: String,
    val pictureUrl: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
