package ru.merkulyevsasha.userinfo.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.scopes.FragmentScoped
import ru.merkulyevsasha.core.domain.UsersInteractor
import ru.merkulyevsasha.core.preferences.KeyValueStorage
import ru.merkulyevsasha.core.repositories.NewsDatabaseRepository
import ru.merkulyevsasha.core.repositories.UsersApiRepository
import ru.merkulyevsasha.userinfo.domain.UsersInteractorImpl

@Module
@InstallIn(FragmentComponent::class)
class UserInfoModule {

    @FragmentScoped
    @Provides
    fun providesUsersInteractor(
        usersApiRepository: UsersApiRepository,
        keyValueStorage: KeyValueStorage,
        db: NewsDatabaseRepository
    ): UsersInteractor {
        return UsersInteractorImpl(
            usersApiRepository,
            keyValueStorage,
            db
        )
    }
}