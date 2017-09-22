package ru.merkulyevsasha.news;

import android.app.Application;

import com.evernote.android.job.JobManager;

import ru.merkulyevsasha.news.di.AppComponent;
import ru.merkulyevsasha.news.di.AppModule;
import ru.merkulyevsasha.news.di.DaggerAppComponent;
import ru.merkulyevsasha.news.newsjobs.NewsJobCreator;

/**
 * Created by sasha_merkulev on 21.09.2017.
 */

public class NewsApp extends Application {
    private static AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        component = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();

        JobManager.create(this).addJobCreator(new NewsJobCreator());

    }

    public static AppComponent getComponent() {
        return component;
    }
}
