package ru.merkulyevsasha.database.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import io.reactivex.Single
import ru.merkulyevsasha.database.entities.ArticleEntity
import java.util.*

@Dao
interface ArticleDao {
    @Query("select * from articles order by pubDate desc")
    fun getArticles(): Single<List<ArticleEntity>>

    @Query("select * from articles where articleId = :articleId order by pubDate desc")
    fun getArticle(articleId: Int): Single<ArticleEntity>

    @Query("select * from articles where isUserLiked or isUserCommented or isUserDisliked order by lastActivityDate desc")
    fun getUserActivityArticles(): Single<List<ArticleEntity>>

    @Query("delete from articles where pubDate < :cleanDate and lastActivityDate < :cleanDate and not (isUserLiked or isUserCommented or isUserDisliked)")
    fun removeOldNotUserActivityArticles(cleanDate: Date)

    @Query("delete from articles where pubDate < :cleanDate and lastActivityDate < :cleanDate and (isUserLiked or isUserCommented or isUserDisliked)")
    fun removeOldUserActivityArticles(cleanDate: Date)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(items: List<ArticleEntity>)

    @Update
    fun update(item: ArticleEntity)
}
