package ru.merkulyevsasha.news

import android.app.Application
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import com.facebook.stetho.Stetho
import com.google.android.gms.ads.MobileAds
import com.squareup.leakcanary.LeakCanary
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NewsApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);


        MobileAds.initialize(this, getString(R.string.APP_ID))

        if (BuildConfig.DEBUG_MODE) {
            Stetho.initializeWithDefaults(this)

//            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
//                .detectDiskReads()
//                .detectDiskWrites()
//                .detectNetwork()   // or .detectAll() for all detectable problems
//                .penaltyLog()
//                .build())
//
//            StrictMode.setVmPolicy(VmPolicy.Builder()
//                .detectLeakedSqlLiteObjects()
//                //.detectLeakedClosableObjects()
//                .penaltyLog()
//                .penaltyDeath()
//                .build())
        }

    }
}
