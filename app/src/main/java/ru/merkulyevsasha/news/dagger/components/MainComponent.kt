package ru.merkulyevsasha.news.dagger.components

import dagger.Subcomponent
import ru.merkulyevsasha.news.dagger.modules.MainModule
import ru.merkulyevsasha.news.dagger.scopes.MainScope

@MainScope
@Subcomponent(modules = arrayOf(MainModule::class))
interface MainComponent {

    @Subcomponent.Builder
    interface Builder {
        fun build(): MainComponent
    }
}
