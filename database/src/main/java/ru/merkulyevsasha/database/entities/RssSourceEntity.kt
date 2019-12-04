package ru.merkulyevsasha.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "sources", indices = [Index("sourceName")])
data class RssSourceEntity(
    @PrimaryKey
    val sourceId: String,
    val sourceName: String
)