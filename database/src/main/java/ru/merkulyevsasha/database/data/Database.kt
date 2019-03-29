package ru.merkulyevsasha.database.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import ru.merkulyevsasha.database.dao.ArticleCommentsDao
import ru.merkulyevsasha.database.dao.ArticleDao
import ru.merkulyevsasha.database.entities.ArticleCommentsEntity
import ru.merkulyevsasha.database.entities.ArticleEntity

@Database(entities = [ArticleEntity::class, ArticleCommentsEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class Database: RoomDatabase() {
    abstract val articleDao: ArticleDao
    abstract val articleCommentsDao: ArticleCommentsDao
}
