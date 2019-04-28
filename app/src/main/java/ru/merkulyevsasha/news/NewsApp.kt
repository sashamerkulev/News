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
        serviceLocator = ServiceLocator(this)

        MobileAds.initialize(this, getString(R.string.APP_ID))
        NewsWorkerPeriodicRunner().runWorker()

        if (BuildConfig.DEBUG_MODE) {
            Stetho.initializeWithDefaults(this)
        }
    }

    fun getServiceLocator() : ServiceLocator {
        return serviceLocator
    }
}
