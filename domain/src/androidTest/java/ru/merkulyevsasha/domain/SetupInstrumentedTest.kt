package ru.merkulyevsasha.domain

import android.support.test.InstrumentationRegistry
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import ru.merkulyevsasha.NewsInsrumentalTestRunner
import ru.merkulyevsasha.core.models.RssSource
import ru.merkulyevsasha.database.DatabaseRepositoryImpl
import ru.merkulyevsasha.preferences.KeyValueStorageImpl
import ru.merkulyevsasha.setup.SetupApiRepositoryImpl
import java.util.*

@RunWith(NewsInsrumentalTestRunner::class)
class SetupInstrumentedTest {

    private val appContext = InstrumentationRegistry.getTargetContext()
    private val preferences = KeyValueStorageImpl(appContext)
    private val databaseRepository = DatabaseRepositoryImpl(appContext)
    private val setupApiRepository = SetupApiRepositoryImpl(preferences)
    private val setupInteractor = SetupInteractorImpl(preferences, setupApiRepository, databaseRepository)

    @Before
    fun setUp() {
        preferences.setSetupId("")
        preferences.setAccessToken("")
        databaseRepository.deleteRssSources()
    }

    @Test
    fun registerSetup_saves_setupId_and_token() {
        val setupId = UUID.randomUUID().toString()
        val testObserver = setupInteractor
            .registerSetup(setupId) { UUID.randomUUID().toString() }
            .test()
        NewsInsrumentalTestRunner.testScheduler.triggerActions()

        testObserver
            .assertNoErrors()

        assertThat(preferences.getSetupId()).isEqualTo(setupId)
        assertThat(preferences.getAccessToken()).isNotEmpty()
    }

    @Test
    fun registerSetup_gives_rsssources() {
        val setupId = UUID.randomUUID().toString()
        val testObserver = setupInteractor
            .registerSetup(setupId) { UUID.randomUUID().toString() }
            .test()
        NewsInsrumentalTestRunner.testScheduler.triggerActions()

        testObserver
            .assertNoErrors()
        assertThat<RssSource>(databaseRepository.getRssSources().blockingGet()).isNotEmpty()
    }

}
