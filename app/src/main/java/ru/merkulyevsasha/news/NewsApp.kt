package ru.merkulyevsasha.news

import android.app.Application
import com.facebook.stetho.Stetho
import com.google.android.gms.ads.MobileAds
import com.squareup.leakcanary.LeakCanary
import ru.merkulyevsasha.ServiceLocator

class NewsApp : Application() {

    private lateinit var serviceLocator: ServiceLocator

    override fun onCreate() {
        super.onCreate()
        serviceLocator = ServiceLocator(this, ApplicationRouterImpl(this))

        MobileAds.initialize(this, getString(R.string.APP_ID))
//        NewsWorkerPeriodicRunner().runWorker()

        if (BuildConfig.DEBUG_MODE) {
            Stetho.initializeWithDefaults(this)
        }

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }

    fun getServiceLocator(): ServiceLocator {
        return serviceLocator
    }
}
