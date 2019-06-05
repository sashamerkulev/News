package ru.merkulyevsasha.news

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import com.facebook.stetho.Stetho
import com.google.android.gms.ads.MobileAds
import com.squareup.leakcanary.LeakCanary
import ru.merkulyevsasha.RequireServiceLocator
import ru.merkulyevsasha.ServiceLocator
import ru.merkulyevsasha.news.presentation.common.MainActivityRouterImpl
import ru.merkulyevsasha.news.presentation.main.MainActivity
import ru.merkulyevsasha.news.presentation.splash.SplashActivity

class NewsApp : Application() {

    private lateinit var serviceLocator: ServiceLocator

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(LifeCycleCallbacks())

        MobileAds.initialize(this, getString(R.string.APP_ID))
//        NewsWorkerPeriodicRunner().runWorker()

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

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }

    inner class LifeCycleCallbacks : FragmentManager.FragmentLifecycleCallbacks(), ActivityLifecycleCallbacks {

        override fun onFragmentAttached(fm: FragmentManager, f: Fragment, context: Context) {
            super.onFragmentAttached(fm, f, context)

            if (f is RequireServiceLocator) {
                f.setServiceLocator(serviceLocator)
            }
        }

        override fun onFragmentDetached(fm: FragmentManager, f: Fragment) {
            super.onFragmentDetached(fm, f)
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
                if (activityInstance.javaClass.simpleName == SplashActivity::class.java.simpleName) {
                    if (activityInstance is RequireServiceLocator) {
                        val serviceLocator = ServiceLocator(this@NewsApp, ApplicationRouterImpl(this@NewsApp))
                        activityInstance.setServiceLocator(serviceLocator)
                    }
                    return@let
                }
                if (activityInstance.javaClass.simpleName == MainActivity::class.java.simpleName) {
                    val supportFragmentManager = (activity as AppCompatActivity).supportFragmentManager
                    supportFragmentManager.registerFragmentLifecycleCallbacks(this, true)
                    val mainActivityRouter = MainActivityRouterImpl(supportFragmentManager)
                    serviceLocator = ServiceLocator(this@NewsApp, ApplicationRouterImpl(this@NewsApp), mainActivityRouter)
                }
                if (activityInstance is RequireServiceLocator) {
                    activityInstance.setServiceLocator(serviceLocator)
                }
            }
        }
    }
}
