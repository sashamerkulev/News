package ru.merkulyevsasha.sl

import android.content.Context
import ru.merkulyevsasha.core.NewsDistributor
import ru.merkulyevsasha.core.ResourceProvider
import ru.merkulyevsasha.core.ServiceLocator
import ru.merkulyevsasha.core.domain.ArticleCommentsInteractor
import ru.merkulyevsasha.core.domain.ArticlesInteractor
import ru.merkulyevsasha.core.domain.SetupInteractor
import ru.merkulyevsasha.core.domain.UsersInteractor
import ru.merkulyevsasha.core.preferences.KeyValueStorage
import ru.merkulyevsasha.core.repositories.ArticleCommentsApiRepository
import ru.merkulyevsasha.core.repositories.ArticlesApiRepository
import ru.merkulyevsasha.core.repositories.NewsDatabaseRepository
import ru.merkulyevsasha.core.repositories.SetupApiRepository
import ru.merkulyevsasha.core.repositories.UsersApiRepository
import ru.merkulyevsasha.core.routers.MainActivityRouter
import ru.merkulyevsasha.core.routers.MainFragmentRouter
import ru.merkulyevsasha.coreandroid.providers.ResourceProviderImpl
import ru.merkulyevsasha.data.database.DatabaseRepositoryImpl
import ru.merkulyevsasha.data.database.NewsRoomDatabaseSourceCreator
import ru.merkulyevsasha.data.network.articles.ArticlesApiRepositoryImpl
import ru.merkulyevsasha.data.network.articlecomments.ArticleCommentsApiRepositoryImpl
import ru.merkulyevsasha.data.network.setup.SetupApiRepositoryImpl
import ru.merkulyevsasha.data.network.users.UsersApiRepositoryImpl
import ru.merkulyevsasha.domain.ArticleCommentsInteractorImpl
import ru.merkulyevsasha.domain.ArticlesInteractorImpl
import ru.merkulyevsasha.domain.NewsDistributorImpl
import ru.merkulyevsasha.domain.SetupInteractorImpl
import ru.merkulyevsasha.domain.UsersInteractorImpl
import ru.merkulyevsasha.domain.mappers.SourceNameMapper
import ru.merkulyevsasha.preferences.KeyValueStorageImpl

class ServiceLocatorImpl private constructor(context: Context) : ServiceLocator {

    companion object {
        private var instance: ServiceLocator? = null
        fun getInstance(context: Context): ServiceLocator {
            if (instance == null) {
                instance = ServiceLocatorImpl(context)
            }
            return instance!!
        }
    }

    private val maps = HashMap<Any, Any>()

    init {
        val prefs = KeyValueStorageImpl(context)
        val resourceProvider = ResourceProviderImpl(context)
        val newsDatabaseSource = NewsRoomDatabaseSourceCreator.create(context, BuildConfig.DB_NAME)
        maps[KeyValueStorage::class.java] = prefs
        maps[ResourceProvider::class.java] = resourceProvider
        maps[NewsDistributor::class.java] = NewsDistributorImpl(context, resourceProvider)
        maps[SetupApiRepository::class.java] = SetupApiRepositoryImpl(prefs, BuildConfig.API_URL, BuildConfig.DEBUG_MODE)
        maps[ArticlesApiRepository::class.java] = ArticlesApiRepositoryImpl(prefs, BuildConfig.API_URL, BuildConfig.DEBUG_MODE)
        maps[ArticleCommentsApiRepository::class.java] = ArticleCommentsApiRepositoryImpl(prefs, BuildConfig.API_URL, BuildConfig.DEBUG_MODE)
        maps[UsersApiRepository::class.java] = UsersApiRepositoryImpl(prefs, BuildConfig.API_URL, BuildConfig.DEBUG_MODE)
        maps[NewsDatabaseRepository::class.java] = DatabaseRepositoryImpl(newsDatabaseSource, prefs, BuildConfig.API_URL)
    }

    override fun <T> set(clazz: Class<T>, instance: Any) {
        maps[clazz] = instance
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(clazz: Class<T>): T {
        if (maps.containsKey(clazz)) {
            return maps[clazz] as T
        }
        when (clazz) {
            ArticlesInteractor::class.java -> maps[clazz] = ArticlesInteractorImpl(
                getArticlesApiRepository(),
                getPreferences(),
                getDatabaseRepository(),
                SourceNameMapper(getDatabaseRepository())
            )
            UsersInteractor::class.java -> maps[clazz] = UsersInteractorImpl(
                getUsersApiRepository(),
                getPreferences()
            )
            ArticleCommentsInteractor::class.java -> maps[clazz] = ArticleCommentsInteractorImpl(
                getArticlesApiRepository(),
                getArticleCommentsApiRepository(),
                getPreferences(),
                getDatabaseRepository(),
                SourceNameMapper(getDatabaseRepository())
            )
            SetupInteractor::class.java -> maps[clazz] = SetupInteractorImpl(
                getPreferences(),
                getSetupApiRepository(),
                getDatabaseRepository()
            )
        }
        return maps[clazz] as T
    }

    override fun <T> release(clazz: Class<T>) {
        if (maps.containsKey(clazz)) {
            maps.remove(clazz)
        }
    }

    override fun releaseAll() {
        maps.clear()
        instance = null
    }

    override fun addFragmentRouter(mainFragmentRouter: MainFragmentRouter) {
        maps[MainFragmentRouter::class.java] = mainFragmentRouter
    }

    override fun releaseFragmentRouter() {
        maps.remove(MainFragmentRouter::class.java)
    }

    override fun addMainRouter(mainActivityRouter: MainActivityRouter) {
        maps[MainActivityRouter::class.java] = mainActivityRouter
    }

    override fun releaseMainRouter() {
        maps.remove(MainActivityRouter::class.java)
    }

    private fun getArticlesApiRepository(): ArticlesApiRepository {
        return maps[ArticlesApiRepository::class.java] as ArticlesApiRepository
    }

    private fun getArticleCommentsApiRepository(): ArticleCommentsApiRepository {
        return maps[ArticleCommentsApiRepository::class.java] as ArticleCommentsApiRepository
    }

    private fun getUsersApiRepository(): UsersApiRepository {
        return maps[UsersApiRepository::class.java] as UsersApiRepository
    }

    private fun getSetupApiRepository(): SetupApiRepository {
        return maps[SetupApiRepository::class.java] as SetupApiRepository
    }

    private fun getDatabaseRepository(): NewsDatabaseRepository {
        return maps[NewsDatabaseRepository::class.java] as NewsDatabaseRepository
    }

    private fun getPreferences(): KeyValueStorage {
        return maps[KeyValueStorage::class.java] as KeyValueStorage
    }
}
