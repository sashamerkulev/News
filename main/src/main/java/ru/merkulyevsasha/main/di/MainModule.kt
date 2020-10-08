package ru.merkulyevsasha.main.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import ru.merkulyevsasha.core.domain.SetupInteractor
import ru.merkulyevsasha.core.preferences.KeyValueStorage
import ru.merkulyevsasha.core.repositories.NewsDatabaseRepository
import ru.merkulyevsasha.core.repositories.SetupApiRepository
import ru.merkulyevsasha.main.domain.SetupInteractorImpl

@Module
@InstallIn(ActivityComponent::class)
class MainModule {

    @ActivityScoped
    @Provides
    fun providesSetupInteractor(
        keyValueStorage: KeyValueStorage,
        setupApiRepository: SetupApiRepository,
        newsDatabaseRepository: NewsDatabaseRepository
    ): SetupInteractor {
        return SetupInteractorImpl(
            keyValueStorage,
            setupApiRepository,
            newsDatabaseRepository
        )
    }

}