package ru.merkulyevsasha.news.di;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.merkulyevsasha.news.presentation.main.MainPresenter;

/**
 * Created by sasha_merkulev on 10.07.2017.
 */

@Module
public class PresentersModule {

    @Singleton
    @Provides
    MainPresenter providesMainPresenter() {
        return new MainPresenter();
    }

}
