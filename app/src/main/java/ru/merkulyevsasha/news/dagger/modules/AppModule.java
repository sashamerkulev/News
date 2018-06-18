package ru.merkulyevsasha.news.dagger.modules;


import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;

import com.evernote.android.job.JobCreator;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import ru.merkulyevsasha.news.BuildConfig;
import ru.merkulyevsasha.news.dagger.scopes.MainScope;
import ru.merkulyevsasha.news.dagger.components.MainComponent;
import ru.merkulyevsasha.news.data.db.DatabaseHelper;
import ru.merkulyevsasha.news.data.db.NewsDbRepository;
import ru.merkulyevsasha.news.data.db.NewsDbRepositoryImpl;
import ru.merkulyevsasha.news.data.db.NewsDbRoom;
import ru.merkulyevsasha.news.data.http.HttpReader;
import ru.merkulyevsasha.news.data.prefs.NewsSharedPreferences;
import ru.merkulyevsasha.news.data.utils.NewsConstants;
import ru.merkulyevsasha.news.domain.NewsInteractor;
import ru.merkulyevsasha.news.helpers.BroadcastHelper;
import ru.merkulyevsasha.news.newsjobs.NewsJobCreator;
import ru.merkulyevsasha.news.newsservices.HttpService;
import ru.merkulyevsasha.news.presentation.main.MainActivity;

@Module(includes = {AndroidSupportInjectionModule.class}, subcomponents = {MainComponent.class})
public abstract class AppModule {

    @Singleton
    @Provides
    static NewsSharedPreferences providesNewsSharedPreferences(Context context) {
        return new NewsSharedPreferences(context);
    }

    @Singleton
    @Provides
    static NewsConstants providesConst(Context context) {
        return new NewsConstants(context);
    }

    @Singleton
    @Provides
    static NewsDbRoom providesNewsDbRoom(Context context) {
        return Room
                .databaseBuilder(context, NewsDbRoom.class, BuildConfig.DB_NAME)
                .fallbackToDestructiveMigration()
                .build();
    }

    @Singleton
    @Binds
    abstract NewsDbRepository providesNewsDbRepository(NewsDbRepositoryImpl newsDbRepository);

    @Singleton
    @Provides
    static HttpReader providesHttpReader() {
        return new HttpReader();
    }

    @Singleton
    @Provides
    static BroadcastHelper providesBroadcastHelper(Context context) {
        return new BroadcastHelper(context);
    }

    @Singleton
    @Provides
    static NewsInteractor providesNewsInteractor(NewsConstants newsConstants, HttpReader reader, NewsSharedPreferences prefs, NewsDbRepository db) {
        return new NewsInteractor(newsConstants, reader, prefs, db);
    }

    @Singleton
    @Binds
    abstract JobCreator providesJobCreator(NewsJobCreator jobCreator);

    @MainScope
    @ContributesAndroidInjector(modules = MainModule.class)
    abstract MainActivity injectorActivity();

    @MainScope
    @ContributesAndroidInjector(modules = MainModule.class)
    abstract HttpService injectorService();
}
