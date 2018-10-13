package ru.merkulyevsasha.news.dagger.components


import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.merkulyevsasha.news.NewsApp
import ru.merkulyevsasha.news.dagger.modules.AppModule
import ru.merkulyevsasha.news.dagger.modules.AppProvidesModule
import javax.inject.Singleton

@Singleton
@Component(modules = [(AppModule::class), (AppProvidesModule::class)])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder

        fun build(): AppComponent
    }

    fun inject(app: NewsApp)
}
