package ru.merkulyevsasha.domain

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.merkulyevsasha.core.domain.SetupInteractor
import ru.merkulyevsasha.core.models.RssSource
import ru.merkulyevsasha.core.preferences.KeyValueStorage
import ru.merkulyevsasha.core.repositories.DatabaseRepository
import ru.merkulyevsasha.database.DatabaseRepositoryImpl
import ru.merkulyevsasha.preferences.KeyValueStorageImpl
import ru.merkulyevsasha.setup.SetupApiRepositoryImpl
import java.util.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(AndroidJUnit4::class)
class SetupInstrumentedTest {

    @Rule
    @JvmField
    val rxSchedulerRule = RxSchedulerRule()

    private lateinit var setupInteractor: SetupInteractor
    private lateinit var preferences: KeyValueStorage
    private lateinit var databaseRepository: DatabaseRepository

    @Before
    fun setUp() {
        val appContext = InstrumentationRegistry.getTargetContext()

        preferences = KeyValueStorageImpl(appContext)
        databaseRepository = DatabaseRepositoryImpl(appContext)
        val setupApiRepository = SetupApiRepositoryImpl(preferences)
        setupInteractor = SetupInteractorImpl(preferences, setupApiRepository, databaseRepository)

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

        testObserver
            .assertNoErrors()
        assertThat<RssSource>(databaseRepository.getRssSources().blockingGet()).isNotEmpty()
    }

}
