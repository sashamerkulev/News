package ru.merkulyevsasha.domain

import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class RxSchedulerRule2 : TestRule {

    private val testScheduler = Schedulers.trampoline()

    override fun apply(base: Statement?, description: Description?): Statement {
        return object: org.junit.runners.model.Statement() {
            override fun evaluate() {
                RxJavaPlugins.setInitIoSchedulerHandler { testScheduler }
                RxJavaPlugins.initIoScheduler { testScheduler }
                RxJavaPlugins.onIoScheduler(testScheduler)
            }
        }
    }

}
