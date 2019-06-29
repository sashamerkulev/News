package ru.merkulyevsasha.news

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.facebook.stetho.Stetho
import com.google.android.gms.ads.MobileAds
import com.squareup.leakcanary.LeakCanary
import ru.merkulyevsasha.RequireServiceLocator
import ru.merkulyevsasha.ServiceLocatorImpl
import ru.merkulyevsasha.news.presentation.main.MainActivity
import ru.merkulyevsasha.news.presentation.main.MainFragment
import ru.merkulyevsasha.news.presentation.routers.MainActivityRouterImpl
import ru.merkulyevsasha.news.presentation.routers.MainFragmentRouterImpl

class NewsApp : Application() {

    private lateinit var serviceLocator: ServiceLocatorImpl

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);

        registerActivityLifecycleCallbacks(LifeCycleCallbacks())

        MobileAds.initialize(this, getString(R.string.APP_ID))

        if (BuildConfig.DEBUG_MODE) {
            Stetho.initializeWithDefaults(this)

            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()   // or .detectAll() for all detectable problems
                .penaltyLog()
                .build())

            StrictMode.setVmPolicy(VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                //.detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build())
        }

    }

    inner class LifeCycleCallbacks : FragmentManager.FragmentLifecycleCallbacks(), ActivityLifecycleCallbacks {

        override fun onFragmentCreated(fm: FragmentManager, fragment: Fragment, savedInstanceState: Bundle?) {
            if (fragment.javaClass.simpleName == MainFragment::class.java.simpleName) {
                serviceLocator.addFragmentRouter(MainFragmentRouterImpl(fragment.childFragmentManager))
            }
            if (fragment is RequireServiceLocator) {
                fragment.setServiceLocator(serviceLocator)
            }
            super.onFragmentCreated(fm, fragment, savedInstanceState)
        }

        override fun onFragmentDestroyed(fm: FragmentManager, fragment: Fragment) {
            if (fragment.javaClass.simpleName == MainFragment::class.java.simpleName) {
                serviceLocator.releaseFragmentRouter()
            }
            super.onFragmentDestroyed(fm, fragment)
        }

        override fun onActivityPaused(activity: Activity?) {
        }

        override fun onActivityResumed(activity: Activity?) {
        }

        override fun onActivityStarted(activity: Activity?) {
        }

        override fun onActivityDestroyed(activity: Activity?) {
            activity?.let { activityInstance ->
                if (activityInstance.javaClass.simpleName == MainActivity::class.java.simpleName) {
                    val supportFragmentManager = (activity as AppCompatActivity).supportFragmentManager
                    supportFragmentManager.unregisterFragmentLifecycleCallbacks(this)
                    serviceLocator.releaseAll()
                }
            }
        }

        override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
        }

        override fun onActivityStopped(activity: Activity?) {
        }

        override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
            activity?.let { activityInstance ->
                if (activityInstance.javaClass.simpleName == MainActivity::class.java.simpleName) {
                    val supportFragmentManager = (activity as AppCompatActivity).supportFragmentManager
                    supportFragmentManager.registerFragmentLifecycleCallbacks(this, true)
                    val mainActivityRouter = MainActivityRouterImpl(supportFragmentManager)
                    serviceLocator = ServiceLocatorImpl(this@NewsApp, mainActivityRouter)
                }
                if (activityInstance is RequireServiceLocator) {
                    activityInstance.setServiceLocator(serviceLocator)
                }
            }
        }
    }
}
