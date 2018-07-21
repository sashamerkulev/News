package ru.merkulyevsasha.news.data.db.entities


import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

import java.util.Date

@Entity(tableName = "articles", indices = [Index("sourceNavId"), Index("search")])
class ArticleEntity {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var sourceNavId: Int = 0
    var title: String = ""
    var link: String = ""
    var description: String = ""
    var pubDate: Date = Date()
    var category: String = ""
    var search: String = ""
    var pictureUrl: String = ""
}
