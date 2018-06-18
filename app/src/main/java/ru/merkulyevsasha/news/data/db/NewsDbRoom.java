package ru.merkulyevsasha.news.data.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

@Database(entities = {ArticleEntity.class}, version = 1, exportSchema = false)
@TypeConverters(Converters.class)
public abstract class NewsDbRoom extends RoomDatabase {
    public abstract ArticleDao getArticleDao();
}
