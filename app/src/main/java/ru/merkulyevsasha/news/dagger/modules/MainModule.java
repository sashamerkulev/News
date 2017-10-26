package ru.merkulyevsasha.news.dagger.modules;


import dagger.Module;
import dagger.Provides;
import ru.merkulyevsasha.news.dagger.scopes.MainScope;
import ru.merkulyevsasha.news.presentation.main.MainPresenter;

/**
 * Created by sasha_merkulev on 27.10.2017.
 */

@Module
public abstract class MainModule {

    @MainScope
    @Provides
    static MainPresenter providesMainPresenter() {
        return new MainPresenter();
    }

}
