package ru.merkulyevsasha.database.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import io.reactivex.Single
import ru.merkulyevsasha.database.entities.ArticleCommentsEntity

@Dao
interface ArticleCommentsDao {
    @Query("select * from comments order by pubDate desc")
    fun getArticleComments(): Single<List<ArticleCommentsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(items: List<ArticleCommentsEntity>)

    @Update
    fun update(item: ArticleCommentsEntity)
}
