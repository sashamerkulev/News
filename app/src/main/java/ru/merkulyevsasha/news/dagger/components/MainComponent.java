package ru.merkulyevsasha.news.dagger.components;

import dagger.Subcomponent;
import ru.merkulyevsasha.news.dagger.modules.MainModule;
import ru.merkulyevsasha.news.dagger.scopes.MainScope;

/**
 * Created by sasha_merkulev on 27.10.2017.
 */
@MainScope
@Subcomponent(modules = MainModule.class)
public interface MainComponent {

    @Subcomponent.Builder
    interface Builder {
        Builder mainModule(MainModule module);

        MainComponent build();
    }
}
