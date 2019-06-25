package ru.merkulyevsasha.database.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import io.reactivex.Single
import ru.merkulyevsasha.database.entities.ArticleCommentEntity

@Dao
interface ArticleCommentsDao {
    @Query("select * from comments where articleId = :articleId order by pubDate desc")
    fun getArticleComments(articleId: Int): Single<List<ArticleCommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(items: List<ArticleCommentEntity>)

    @Update
    fun update(item: ArticleCommentEntity)

    @Query("update articles set usersCommentCount = usersCommentCount + :commentsCount, isUserCommented = 1 where articleId = :articleId")
    fun updateArticle(articleId: Int, commentsCount: Int)

}
