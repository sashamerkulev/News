package ru.merkulyevsasha.news.data.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface ArticleDao {

    @Insert
    void addListNews(ArticleEntity... items);

    @Query("delete from articles where sourceNavId = :navId")
    void delete(int navId);

    @Query("delete from articles")
    void deleteAll();

    @Query("select * from articles order by pubDate desc")
    Single<List<ArticleEntity>> selectAll();

    @Query("select * from articles where sourceNavId = :navId order by pubDate desc")
    Single<List<ArticleEntity>> selectNavId(int navId);

    @Query("select * from articles where search like :searchTtext order by pubDate desc")
    Single<List<ArticleEntity>> search(String searchTtext);
}
