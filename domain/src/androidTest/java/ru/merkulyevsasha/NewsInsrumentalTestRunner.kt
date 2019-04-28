package ru.merkulyevsasha

import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.TestScheduler
import org.junit.runners.BlockJUnit4ClassRunner

class NewsInsrumentalTestRunner(klass: Class<*>) : BlockJUnit4ClassRunner(klass) {

    init {
        RxJavaPlugins.setInitIoSchedulerHandler { schedulerCallable -> testScheduler }
        RxJavaPlugins.initIoScheduler { testScheduler }
        RxJavaPlugins.onIoScheduler(testScheduler)
    }

    companion object {
        @JvmStatic
        val testScheduler = TestScheduler()
    }
}
