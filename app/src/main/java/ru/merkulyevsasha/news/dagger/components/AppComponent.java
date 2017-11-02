package ru.merkulyevsasha.news.dagger.components;



import android.content.Context;


import com.evernote.android.job.JobCreator;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import ru.merkulyevsasha.news.NewsApp;
import ru.merkulyevsasha.news.dagger.modules.AppModule;
import ru.merkulyevsasha.news.data.utils.NewsConstants;
import ru.merkulyevsasha.news.domain.NewsInteractor;


@Singleton
@Component(modules={AppModule.class})
public interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder context(Context context);

        AppComponent build();
    }

    void inject(NewsApp app);

    JobCreator getJobCreator();
}
