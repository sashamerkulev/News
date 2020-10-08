package ru.merkulyevsasha.sourcelist.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.scopes.FragmentScoped
import ru.merkulyevsasha.core.domain.SourceInteractor
import ru.merkulyevsasha.core.repositories.NewsDatabaseRepository
import ru.merkulyevsasha.sourcelist.domain.SourceInteractorImpl

@Module
@InstallIn(FragmentComponent::class)
class SourceListModule {

    @FragmentScoped
    @Provides
    fun providesSourceInteractor(db: NewsDatabaseRepository): SourceInteractor {
        return SourceInteractorImpl(db)
    }

}