package ru.merkulyevsasha.database.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import io.reactivex.Single
import ru.merkulyevsasha.database.entities.ArticleCommentsEntity

@Dao
interface ArticleCommentsDao {
    @Query("select * from comments order by date desc")
    fun getArticleComments(): Single<List<ArticleCommentsEntity>>
}
