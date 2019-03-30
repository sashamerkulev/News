package ru.merkulyevsasha.news

import android.app.Application
import com.facebook.stetho.Stetho
import com.google.android.gms.ads.MobileAds
import ru.merkulyevsasha.ServiceLocator
import ru.merkulyevsasha.news.newsjobs.NewsWorkerPeriodicRunner

class NewsApp : Application() {

    private lateinit var serviceLocator : ServiceLocator

    override fun onCreate() {
        super.onCreate()

//        val clazz = serviceLocator.get(ArticlesInteractor::class.java)
//        val clazz2 = serviceLocator.get(UsersInteractor::class.java)
//        val clazz3 = serviceLocator.get(ArticleCommentsInteractor::class.java)

        MobileAds.initialize(this, getString(R.string.APP_ID))
        NewsWorkerPeriodicRunner().runWorker()

        if (BuildConfig.DEBUG_MODE) {
            Stetho.initializeWithDefaults(this)
        }
        serviceLocator = ServiceLocator(this)
    }

    fun getServiceLocator() : ServiceLocator {
        return serviceLocator
    }
}
