package ru.merkulyevsasha.news.dagger.modules;


import android.arch.persistence.room.Room;
import android.content.Context;

import com.evernote.android.job.JobCreator;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import okhttp3.OkHttpClient;
import ru.merkulyevsasha.news.BuildConfig;
import ru.merkulyevsasha.news.dagger.scopes.MainScope;
import ru.merkulyevsasha.news.dagger.components.MainComponent;
import ru.merkulyevsasha.news.data.NewsDbRepository;
import ru.merkulyevsasha.news.data.NewsDbRepositoryImpl;
import ru.merkulyevsasha.news.data.db.NewsDbRoom;
import ru.merkulyevsasha.news.data.http.HttpReader;
import ru.merkulyevsasha.news.data.http.HttpReaderImpl;
import ru.merkulyevsasha.news.data.prefs.NewsSharedPreferences;
import ru.merkulyevsasha.news.data.prefs.NewsSharedPreferencesImpl;
import ru.merkulyevsasha.news.data.utils.NewsConstants;
import ru.merkulyevsasha.news.domain.NewsInteractor;
import ru.merkulyevsasha.news.domain.NewsInteractorImpl;
import ru.merkulyevsasha.news.helpers.BroadcastHelper;
import ru.merkulyevsasha.news.newsjobs.NewsJobCreator;
import ru.merkulyevsasha.news.presentation.main.MainActivity;
import ru.merkulyevsasha.news.presentation.main.MainPresenter;

@Module(includes = {AndroidSupportInjectionModule.class}, subcomponents = {MainComponent.class})
public abstract class AppModule {

    @Singleton
    @Provides
    static NewsConstants providesConst(Context context) {
        return new NewsConstants(context);
    }

    @Singleton
    @Provides
    static OkHttpClient providesOkHttpClient(){
        return new OkHttpClient();
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
    @Provides
    static BroadcastHelper providesBroadcastHelper(Context context) {
        return new BroadcastHelper(context);
    }

    @Singleton
    @Provides
    static MainPresenter providePres(NewsInteractorImpl news) {
        return new MainPresenter(news);
    }

    @Singleton
    @Binds
    abstract NewsSharedPreferences bindsNewsSharedPreferences(NewsSharedPreferencesImpl impl);

    @Singleton
    @Binds
    abstract NewsDbRepository bindsNewsDbRepository(NewsDbRepositoryImpl newsDbRepository);

    @Singleton
    @Binds
    abstract HttpReader bindsHttpReader(HttpReaderImpl impl);

    @Singleton
    @Binds
    abstract NewsInteractor bindsNewsInteractor(NewsInteractorImpl impl);

    @Singleton
    @Binds
    abstract JobCreator bindsJobCreator(NewsJobCreator jobCreator);

    @MainScope
    @ContributesAndroidInjector(modules = MainModule.class)
    abstract MainActivity injectorActivity();
}
