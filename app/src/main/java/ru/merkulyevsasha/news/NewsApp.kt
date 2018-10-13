package ru.merkulyevsasha.news

import android.app.Activity
import android.app.Application
import com.evernote.android.job.JobManager
import com.google.android.gms.ads.MobileAds
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import ru.merkulyevsasha.news.dagger.components.DaggerAppComponent
import javax.inject.Inject

/**
 * Created by sasha_merkulev on 21.09.2017.
 */

class NewsApp : Application(), HasActivityInjector {

    @Inject internal lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()

        val component = DaggerAppComponent
            .builder()
            .context(this)
            .build()

        component.inject(this)

        MobileAds.initialize(this, getString(R.string.APP_ID))
        JobManager.create(this).addJobCreator(component.jobCreator)
    }

    override fun activityInjector(): AndroidInjector<Activity>? {
        return activityInjector
    }
}
