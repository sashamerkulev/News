package ru.merkulyevsasha.news.dagger.components


import android.content.Context

import com.evernote.android.job.JobCreator

import javax.inject.Singleton

import dagger.BindsInstance
import dagger.Component
import ru.merkulyevsasha.news.NewsApp
import ru.merkulyevsasha.news.dagger.modules.AppModule
import ru.merkulyevsasha.news.dagger.modules.AppProvidesModule

@Singleton
@Component(modules = [(AppModule::class), (AppProvidesModule::class)])
interface AppComponent {

    val jobCreator: JobCreator

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder

        fun build(): AppComponent
    }

    fun inject(app: NewsApp)
}
