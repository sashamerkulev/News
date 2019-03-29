package ru.merkulyevsasha

import android.content.Context
import ru.merkulyevsasha.core.preferences.SharedPreferences
import ru.merkulyevsasha.core.repositories.ArticlesApiRepository
import ru.merkulyevsasha.core.repositories.DatabaseRepository
import ru.merkulyevsasha.core.repositories.UsersApiRepository
import ru.merkulyevsasha.database.DatabaseRepositoryImpl
import ru.merkulyevsasha.domain.RssServiceInteractorImpl
import ru.merkulyevsasha.network.ArticlesApiRepositoryImpl
import ru.merkulyevsasha.network.UsersApiRepositoryImpl
import ru.merkulyevsasha.preferences.SharedPreferencesImpl

class ServiceLocator(context: Context) {

    private val maps = HashMap<Any, Any>()

    init {
        val prefs = SharedPreferencesImpl(context)
        maps[SharedPreferences::class.java] = prefs
        maps[ArticlesApiRepository::class.java] = ArticlesApiRepositoryImpl(prefs)
        maps[UsersApiRepository::class.java] = UsersApiRepositoryImpl(prefs)
        maps[DatabaseRepository::class.java] = DatabaseRepositoryImpl(context)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> get(clazz: Class<T>): T {

        if (clazz == RssServiceInteractorImpl::class.java && !maps.containsKey(clazz)) {
            maps[clazz] = RssServiceInteractorImpl(
                getArticlesApiRepository(),
                getUsersApiRepository(),
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

    private fun getDatabaseRepository(): DatabaseRepository {
        return maps[DatabaseRepository::class.java] as DatabaseRepository
    }

}

