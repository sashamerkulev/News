package ru.merkulyevsasha.news.di;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.merkulyevsasha.news.data.db.DatabaseHelper;
import ru.merkulyevsasha.news.data.http.HttpReader;
import ru.merkulyevsasha.news.domain.NewsInteractor;

/**
 * Created by sasha_merkulev on 10.07.2017.
 */

@Module
public class InteractorsModule {

    @Singleton
    @Provides
    NewsInteractor providesNewsInteractor(HttpReader reader, DatabaseHelper db) {
        return new NewsInteractor(reader, db);
    }

}
