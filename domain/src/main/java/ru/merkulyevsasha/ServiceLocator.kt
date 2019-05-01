package ru.merkulyevsasha

import android.content.Context
import ru.merkulyevsasha.articles.ArticlesApiRepositoryImpl
import ru.merkulyevsasha.core.domain.ArticleCommentsInteractor
import ru.merkulyevsasha.core.domain.ArticlesInteractor
import ru.merkulyevsasha.core.domain.SetupInteractor
import ru.merkulyevsasha.core.domain.UsersInteractor
import ru.merkulyevsasha.core.preferences.KeyValueStorage
import ru.merkulyevsasha.core.repositories.ArticlesApiRepository
import ru.merkulyevsasha.core.repositories.DatabaseRepository
import ru.merkulyevsasha.core.repositories.SetupApiRepository
import ru.merkulyevsasha.core.repositories.UsersApiRepository
import ru.merkulyevsasha.database.DatabaseRepositoryImpl
import ru.merkulyevsasha.domain.ArticleCommentsInteractorImpl
import ru.merkulyevsasha.domain.ArticlesInteractorImpl
import ru.merkulyevsasha.domain.SetupInteractorImpl
import ru.merkulyevsasha.domain.UsersInteractorImpl
import ru.merkulyevsasha.preferences.KeyValueStorageImpl
import ru.merkulyevsasha.setup.SetupApiRepositoryImpl
import ru.merkulyevsasha.users.UsersApiRepositoryImpl

class ServiceLocator(context: Context) {

    private val maps = HashMap<Any, Any>()

    init {
        val prefs = KeyValueStorageImpl(context)
        maps[KeyValueStorage::class.java] = prefs
        maps[SetupApiRepository::class.java] = SetupApiRepositoryImpl(prefs)
        maps[ArticlesApiRepository::class.java] = ArticlesApiRepositoryImpl(prefs)
        maps[UsersApiRepository::class.java] = UsersApiRepositoryImpl(prefs)
        maps[DatabaseRepository::class.java] = DatabaseRepositoryImpl(context)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> get(clazz: Class<T>): T {
        if (maps.containsKey(clazz)) {
            return maps[clazz] as T
        }
        when (clazz) {
            ArticlesInteractor::class.java -> maps[clazz] = ArticlesInteractorImpl(
                getArticlesApiRepository(),
                getPreferences(),
                getDatabaseRepository()
            )
            UsersInteractor::class.java -> maps[clazz] = UsersInteractorImpl(
                getUsersApiRepository(),
                getDatabaseRepository()
            )
            ArticleCommentsInteractor::class.java -> maps[clazz] = ArticleCommentsInteractorImpl(
                getArticlesApiRepository(),
                getDatabaseRepository()
            )
            SetupInteractor::class.java -> maps[clazz] = SetupInteractorImpl(
                getPreferences(),
                getSetupApiRepository(),
                getDatabaseRepository()
            )
        }
        return maps[clazz] as T
    }

    private fun getArticlesApiRepository(): ArticlesApiRepository {
        return maps[ArticlesApiRepository::class.java] as ArticlesApiRepository
    }

    private fun getUsersApiRepository(): UsersApiRepository {
        return maps[UsersApiRepository::class.java] as UsersApiRepository
    }

    private fun getSetupApiRepository(): SetupApiRepository {
        return maps[SetupApiRepository::class.java] as SetupApiRepository
    }

    private fun getDatabaseRepository(): DatabaseRepository {
        return maps[DatabaseRepository::class.java] as DatabaseRepository
    }

    private fun getPreferences(): KeyValueStorage {
        return maps[KeyValueStorage::class.java] as KeyValueStorage
    }
}

