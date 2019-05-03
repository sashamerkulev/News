package ru.merkulyevsasha.database.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import io.reactivex.Single
import ru.merkulyevsasha.database.entities.ArticleEntity

@Dao
interface ArticleDao {
    @Query("select * from articles order by pubDate desc")
    fun getArticles(): Single<List<ArticleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(items: List<ArticleEntity>)

    @Update
    fun update(item: ArticleEntity)
}
