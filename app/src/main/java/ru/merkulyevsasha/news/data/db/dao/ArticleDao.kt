package ru.merkulyevsasha.news.data.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

import io.reactivex.Single
import ru.merkulyevsasha.news.data.db.entities.ArticleEntity

@Dao
interface ArticleDao {

    @Query("select * from articles order by pubDate desc limit 1")
    fun getLastArticle(): ArticleEntity?

    @Insert
    fun addListNews(vararg items: ArticleEntity)

    @Query("delete from articles where sourceNavId = :navId")
    fun delete(navId: Int)

    @Query("delete from articles")
    fun deleteAll()

    @Query("select * from articles order by pubDate desc")
    fun selectAll(): Single<List<ArticleEntity>>

    @Query("select * from articles where sourceNavId = :navId order by pubDate desc")
    fun selectNavId(navId: Int): Single<List<ArticleEntity>>

    @Query("select * from articles where search like :searchTtext order by pubDate desc")
    fun search(searchTtext: String): Single<List<ArticleEntity>>
}
