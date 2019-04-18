package ru.merkulyevsasha

import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.runners.BlockJUnit4ClassRunner
import org.junit.runners.model.InitializationError

class NewsTestRunner
/**
 * Creates a BlockJUnit4ClassRunner to run `klass`
 *
 * @param klass
 * @throws InitializationError if the test class is malformed.
 */
@Throws(InitializationError::class)
constructor(klass: Class<*>) : BlockJUnit4ClassRunner(klass) {

    private val testScheduler = Schedulers.trampoline()

    init {
        RxJavaPlugins.setInitIoSchedulerHandler { schedulerCallable -> testScheduler }
        RxJavaPlugins.initIoScheduler { testScheduler }
        RxJavaPlugins.onIoScheduler(testScheduler)
    }
}
