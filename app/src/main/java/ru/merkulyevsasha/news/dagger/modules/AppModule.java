package ru.merkulyevsasha.news.dagger.modules;


import android.content.Context;

import com.evernote.android.job.JobCreator;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import ru.merkulyevsasha.news.dagger.scopes.MainScope;
import ru.merkulyevsasha.news.dagger.components.MainComponent;
import ru.merkulyevsasha.news.data.db.DatabaseHelper;
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
    static DatabaseHelper providesDatabaseHelper(Context context) {
        return new DatabaseHelper(context);
    }

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
    static NewsInteractor providesNewsInteractor(HttpReader reader, DatabaseHelper db) {
        return new NewsInteractor(reader, db);
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
