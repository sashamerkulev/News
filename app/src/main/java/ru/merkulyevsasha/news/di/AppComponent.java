package ru.merkulyevsasha.news.di;



import javax.inject.Singleton;

import dagger.Component;
import ru.merkulyevsasha.news.presentation.main.MainActivity;
import ru.merkulyevsasha.news.presentation.webview.WebViewActivity;
import ru.merkulyevsasha.news.newsservices.HttpService;
import ru.merkulyevsasha.news.newsjobs.NewsJob;


@Singleton
@Component(modules={AppModule.class, DataModule.class, InteractorsModule.class})
public interface AppComponent {
    void inject(MainActivity context);
    void inject(WebViewActivity context);
    void inject(HttpService context);
    void inject(NewsJob context);
}
