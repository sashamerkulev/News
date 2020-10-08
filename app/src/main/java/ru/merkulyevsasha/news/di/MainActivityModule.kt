package ru.merkulyevsasha.news.di

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import ru.merkulyevsasha.core.Logger
import ru.merkulyevsasha.core.routers.MainActivityRouter
import ru.merkulyevsasha.news.presentation.main.MainActivity
import ru.merkulyevsasha.news.presentation.routers.MainActivityRouterImpl
import javax.inject.Qualifier

@Module
@InstallIn(ActivityComponent::class)
class MainActivityModule {

    @ActivityScoped
    @Provides
    @ActivityFragmentManager
    fun provideFragmentManager(activity: FragmentActivity): FragmentManager {
        return (activity as MainActivity).supportFragmentManager
    }

    @ActivityScoped
    @Provides
    fun providesMainActivityRouter(
        @ActivityFragmentManager fragmentManager: FragmentManager,
        logger: Logger
    ): MainActivityRouter {
        return MainActivityRouterImpl(
            fragmentManager,
            logger
        )
    }

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class ActivityFragmentManager

}