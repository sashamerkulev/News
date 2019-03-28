package ru.merkulyevsasha.news.dagger.modules


import android.arch.persistence.room.Room
import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import okhttp3.OkHttpClient
import ru.merkulyevsasha.news.BuildConfig
import ru.merkulyevsasha.news.dagger.components.MainComponent
import ru.merkulyevsasha.news.dagger.scopes.MainScope
import ru.merkulyevsasha.news.data.NewsRepository
import ru.merkulyevsasha.news.data.NewsRepositoryImpl
import ru.merkulyevsasha.news.data.db.DbDataSource
import ru.merkulyevsasha.news.data.db.DbDataSourceImpl
import ru.merkulyevsasha.news.data.db.NewsDbRoom
import ru.merkulyevsasha.news.data.http.HttpDataSource
import ru.merkulyevsasha.news.data.http.HttpDataSourceImpl
import ru.merkulyevsasha.news.data.prefs.NewsSharedPreferences
import ru.merkulyevsasha.news.data.prefs.NewsSharedPreferencesImpl
import ru.merkulyevsasha.news.data.utils.NewsConstants
import ru.merkulyevsasha.news.domain.NewsInteractor
import ru.merkulyevsasha.news.domain.NewsInteractorImpl
import ru.merkulyevsasha.news.helpers.BroadcastHelper
import ru.merkulyevsasha.news.newsjobs.BackgroundPeriodicWorker
import ru.merkulyevsasha.news.newsjobs.BackgroundWorker
import ru.merkulyevsasha.news.newsjobs.NewsWorkerPeriodicRunner
import ru.merkulyevsasha.news.newsjobs.NewsWorkerRunner
import ru.merkulyevsasha.news.presentation.main.MainActivity
import javax.inject.Singleton

@Module(includes = [(AndroidSupportInjectionModule::class)], subcomponents = [(MainComponent::class)])
abstract class AppModule {

    @Singleton
    @Binds
    internal abstract fun bindsNewsSharedPreferences(impl: NewsSharedPreferencesImpl): NewsSharedPreferences

    @Singleton
    @Binds
    internal abstract fun bindsNewsRepository(ompl: NewsRepositoryImpl): NewsRepository

    @Singleton
    @Binds
    internal abstract fun bindsHttpDataSource(impl: HttpDataSourceImpl): HttpDataSource

    @Singleton
    @Binds
    internal abstract fun bindsDbDataSource(impl: DbDataSourceImpl): DbDataSource

    @Singleton
    @Binds
    internal abstract fun bindsNewsInteractor(impl: NewsInteractorImpl): NewsInteractor

    @Singleton
    @Binds
    internal abstract fun bindsNewsWorkerRunner(impl: NewsWorkerPeriodicRunner): BackgroundPeriodicWorker

    @Singleton
    @Binds
    internal abstract fun bindsBackgroundWorker(impl: NewsWorkerRunner): BackgroundWorker

    @MainScope
    @ContributesAndroidInjector(modules = arrayOf(MainModule::class))
    internal abstract fun injectorActivity(): MainActivity
}

@Module
class AppProvidesModule {
    @Singleton
    @Provides
    internal fun providesConst(context: Context): NewsConstants {
        return NewsConstants(context)
    }

    @Singleton
    @Provides
    internal fun providesOkHttpClient(): OkHttpClient {
        return OkHttpClient()
    }

    @Singleton
    @Provides
    internal fun providesNewsDbRoom(context: Context): NewsDbRoom {
        return Room
            .databaseBuilder(context, NewsDbRoom::class.java, BuildConfig.DB_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    internal fun providesBroadcastHelper(context: Context): BroadcastHelper {
        return BroadcastHelper(context)
    }
}