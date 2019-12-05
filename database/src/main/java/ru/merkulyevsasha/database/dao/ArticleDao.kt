package ru.merkulyevsasha.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.reactivex.Single
import ru.merkulyevsasha.database.entities.ArticleEntity
import java.util.*

@Dao
interface ArticleDao {
    @Query("select a.* from articles a join sources s on s.sourceId = a.sourceId where s.checked order by a.pubDate desc ")
    fun getArticles(): Single<List<ArticleEntity>>

    @Query("select a.* from articles a join sources s on s.sourceId = a.sourceId where s.checked and  a.search like :searchText order by a.pubDate desc")
    fun searchArticles(searchText: String): Single<List<ArticleEntity>>

    @Query("select * from articles where articleId = :articleId order by pubDate desc")
    fun getArticle(articleId: Int): Single<ArticleEntity>

    @Query("select * from articles where isUserLiked or isUserCommented or isUserDisliked order by pubDate desc")
    fun getUserActivityArticles(): Single<List<ArticleEntity>>

    @Query("select * from articles where search like :searchText and (isUserLiked or isUserCommented or isUserDisliked) order by pubDate desc")
    fun searchUserActivitiesArticles(searchText: String): Single<List<ArticleEntity>>

    @Query("select * from articles where sourceId = :sourceId order by pubDate desc")
    fun getSourceArticles(sourceId: String): Single<List<ArticleEntity>>

    @Query("select * from articles where search like :searchText and sourceId = :sourceId order by pubDate desc")
    fun searchSourceArticles(sourceId: String, searchText: String): Single<List<ArticleEntity>>

    @Query("delete from articles where pubDate < :cleanDate and lastActivityDate < :cleanDate " +
        "and not (isUserLiked or isUserCommented or isUserDisliked)")
    fun removeOldNotUserActivityArticles(cleanDate: Date)

    @Query("delete from articles where pubDate < :cleanDate and lastActivityDate < :cleanDate " +
        "and (isUserLiked or isUserCommented or isUserDisliked)")
    fun removeOldUserActivityArticles(cleanDate: Date)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(items: List<ArticleEntity>)

    @Update
    fun update(item: ArticleEntity)
}
