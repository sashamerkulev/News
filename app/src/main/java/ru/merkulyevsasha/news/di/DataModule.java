package ru.merkulyevsasha.news.di;


import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.merkulyevsasha.news.data.db.DatabaseHelper;
import ru.merkulyevsasha.news.data.http.HttpReader;
import ru.merkulyevsasha.news.data.prefs.NewsSharedPreferences;
import ru.merkulyevsasha.news.data.utils.NewsConstants;
import ru.merkulyevsasha.news.helpers.BroadcastHelper;


@Module
public class DataModule {

    @Singleton
    @Provides
    NewsSharedPreferences providesNewsSharedPreferences(Context context) {
        return new NewsSharedPreferences(context);
    }

    @Singleton
    @Provides
    NewsConstants providesConst(Context context) {
        return new NewsConstants(context);
    }

    @Singleton
    @Provides
    DatabaseHelper providesDatabaseHelper(Context context) {
        return new DatabaseHelper(context);
    }

    @Singleton
    @Provides
    HttpReader providesHttpReader() {
        return new HttpReader();
    }

    @Singleton
    @Provides
    BroadcastHelper providesBroadcastHelper(Context context) {
        return new BroadcastHelper(context);
    }
}
