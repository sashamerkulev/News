package ru.merkulyevsasha.domain

import io.reactivex.Single
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import ru.merkulyevsasha.core.models.Token
import ru.merkulyevsasha.core.preferences.KeyValueStorage
import ru.merkulyevsasha.core.repositories.DatabaseRepository
import ru.merkulyevsasha.core.repositories.SetupApiRepository
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(JUnit4::class)
class SetupInteractorImplTest {

    @Rule
    @JvmField
    val rxSchedulerRule = RxSchedulerRule2()

    private val preferences = Mockito.mock(KeyValueStorage::class.java)
    private val databaseRepository = Mockito.mock(DatabaseRepository::class.java)
    private val setupApiRepository = Mockito.mock(SetupApiRepository::class.java)

    private val setupInteractor = SetupInteractorImpl(preferences, setupApiRepository, databaseRepository)

    @After
    fun tearDown() {
        Mockito.verifyNoMoreInteractions(preferences, databaseRepository, setupApiRepository)
    }

    @Test
    fun registerSetup_calls_repo_registerSetup_if_setup_id_is_empty() {
        val setupId = UUID.randomUUID().toString()
        val token = UUID.randomUUID().toString()

        Mockito.`when`(preferences.getSetupId()).thenReturn("")
        Mockito.`when`(setupApiRepository.registerSetup(setupId, Mockito.anyString())).thenReturn(Single.just(Token(token)))

        setupInteractor.registerSetup(setupId, getFirebaseId = { UUID.randomUUID().toString() })
            .test()

        Mockito.verify(preferences).getSetupId()
        Mockito.verify(preferences).setSetupId(setupId)
        Mockito.verify(preferences).setAccessToken(token)

        Mockito.verifyNoMoreInteractions(preferences, databaseRepository, setupApiRepository)
    }
}