package ru.merkulyevsasha.articlecomments.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.scopes.FragmentScoped
import ru.merkulyevsasha.articlecomments.domain.ArticleCommentsInteractorImpl
import ru.merkulyevsasha.core.domain.ArticleCommentsInteractor
import ru.merkulyevsasha.core.preferences.KeyValueStorage
import ru.merkulyevsasha.core.repositories.ArticleCommentsApiRepository
import ru.merkulyevsasha.core.repositories.ArticlesApiRepository
import ru.merkulyevsasha.core.repositories.NewsDatabaseRepository

@Module
@InstallIn(FragmentComponent::class)
class ArticleCommentsModule {

    @FragmentScoped
    @Provides
    fun providesArticleCommentsInteractor(
        articlesApiRepository: ArticlesApiRepository,
        articleCommentsApiRepository: ArticleCommentsApiRepository,
        keyValueStorage: KeyValueStorage,
        db: NewsDatabaseRepository
    ): ArticleCommentsInteractor {
        return ArticleCommentsInteractorImpl(
            articlesApiRepository,
            articleCommentsApiRepository,
            keyValueStorage,
            db
        )
    }

}