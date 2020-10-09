package ru.merkulyevsasha.news.di

import android.content.Context
import com.facebook.stetho.okhttp3.StethoInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.merkulyevsasha.articles.domain.ArticlesInteractorImpl
import ru.merkulyevsasha.core.ArticleDistributor
import ru.merkulyevsasha.core.Logger
import ru.merkulyevsasha.core.LoggerImpl
import ru.merkulyevsasha.core.ResourceProvider
import ru.merkulyevsasha.core.domain.ArticlesInteractor
import ru.merkulyevsasha.core.preferences.KeyValueStorage
import ru.merkulyevsasha.core.repositories.ArticleCommentsApiRepository
import ru.merkulyevsasha.core.repositories.ArticlesApiRepository
import ru.merkulyevsasha.core.repositories.NewsDatabaseRepository
import ru.merkulyevsasha.core.repositories.SetupApiRepository
import ru.merkulyevsasha.core.repositories.UsersApiRepository
import ru.merkulyevsasha.coreandroid.providers.ResourceProviderImpl
import ru.merkulyevsasha.dbrepository.database.NewsDatabaseRepositoryImpl
import ru.merkulyevsasha.dbrepository.database.NewsDatabaseSource
import ru.merkulyevsasha.dbrepository.database.NewsRoomDatabaseSourceCreator
import ru.merkulyevsasha.domain.ArticleDistributorImpl
import ru.merkulyevsasha.netrepository.network.articlecomments.ArticleCommentsApiRepositoryImpl
import ru.merkulyevsasha.netrepository.network.articles.ArticlesApiRepositoryImpl
import ru.merkulyevsasha.netrepository.network.base.LoggingInterceptor
import ru.merkulyevsasha.netrepository.network.base.TokenInterceptor
import ru.merkulyevsasha.netrepository.network.setup.SetupApiRepositoryImpl
import ru.merkulyevsasha.netrepository.network.users.UsersApiRepositoryImpl
import ru.merkulyevsasha.news.BuildConfig
import ru.merkulyevsasha.preferences.KeyValueStorageImpl
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun providesLogger(): Logger {
        return LoggerImpl()
    }

    @Singleton
    @Provides
    fun providesResourceProvider(@ApplicationContext context: Context): ResourceProvider {
        return ResourceProviderImpl(context)
    }

    @Singleton
    @Provides
    fun providesKeyValueStorage(@ApplicationContext context: Context): KeyValueStorage {
        return KeyValueStorageImpl(context)
    }

    @Singleton
    @Provides
    fun providesArticleDistributor(
        @ApplicationContext context: Context,
        resourceProvider: ResourceProvider
    ): ArticleDistributor {
        return ArticleDistributorImpl(
            context,
            resourceProvider
        )
    }

    @Singleton
    @Provides
    fun providesOkHttpClient(keyValueStorage: KeyValueStorage): OkHttpClient {
        val builder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG_MODE) {

            builder.addNetworkInterceptor(StethoInterceptor())
        }
        builder.addNetworkInterceptor(LoggingInterceptor())
        builder.addInterceptor(TokenInterceptor(keyValueStorage))
        return builder.build()
    }

    @Singleton
    @Provides
    fun providesRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

    }

    @Singleton
    @Provides
    fun providesSetupApiRepository(keyValueStorage: KeyValueStorage, l: Logger): SetupApiRepository {
        return SetupApiRepositoryImpl(
            keyValueStorage,
            BuildConfig.API_URL,
            BuildConfig.DEBUG_MODE,
            l
        )
    }

    @Singleton
    @Provides
    fun providesArticlesApiRepository(keyValueStorage: KeyValueStorage): ArticlesApiRepository {
        return ArticlesApiRepositoryImpl(
            keyValueStorage,
            BuildConfig.API_URL,
            BuildConfig.DEBUG_MODE
        )
    }

    @Singleton
    @Provides
    fun providesArticleCommentsApiRepository(keyValueStorage: KeyValueStorage): ArticleCommentsApiRepository {
        return ArticleCommentsApiRepositoryImpl(
            keyValueStorage,
            BuildConfig.API_URL,
            BuildConfig.DEBUG_MODE
        )
    }

    @Singleton
    @Provides
    fun providesUsersApiRepository(keyValueStorage: KeyValueStorage): UsersApiRepository {
        return UsersApiRepositoryImpl(
            keyValueStorage,
            BuildConfig.API_URL,
            BuildConfig.DEBUG_MODE
        )
    }

    @Singleton
    @Provides
    fun providesNewsDatabaseSource(@ApplicationContext context: Context): NewsDatabaseSource {
        return NewsRoomDatabaseSourceCreator.create(context, BuildConfig.DB_NAME)
    }

    @Singleton
    @Provides
    fun providesNewsDatabaseRepository(
        keyValueStorage: KeyValueStorage,
        newsDatabaseSource: NewsDatabaseSource
    ): NewsDatabaseRepository {
        return NewsDatabaseRepositoryImpl(
            newsDatabaseSource,
            keyValueStorage,
            BuildConfig.API_URL
        )
    }

    @Singleton
    @Provides
    fun providesArticlesInteractor(
        articlesApiRepository: ArticlesApiRepository,
        keyValueStorage: KeyValueStorage,
        db: NewsDatabaseRepository
    ): ArticlesInteractor {
        return ArticlesInteractorImpl(
            articlesApiRepository,
            keyValueStorage,
            db
        )
    }

}