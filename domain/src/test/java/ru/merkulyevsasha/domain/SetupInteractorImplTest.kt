package ru.merkulyevsasha.domain

import io.reactivex.Single
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import ru.merkulyevsasha.NewsTestRunner
import ru.merkulyevsasha.core.models.Token
import ru.merkulyevsasha.core.preferences.KeyValueStorage
import ru.merkulyevsasha.core.repositories.DatabaseRepository
import ru.merkulyevsasha.core.repositories.SetupApiRepository
import java.util.*

@RunWith(NewsTestRunner::class)
class SetupInteractorImplTest {

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
        val firebaseId = UUID.randomUUID().toString()

        Mockito.`when`(preferences.getSetupId()).thenReturn("")
        Mockito.`when`(setupApiRepository.registerSetup(Mockito.eq(setupId), Mockito.anyString())).thenReturn(Single.just(Token(token)))

        val testObserver = setupInteractor.registerSetup(setupId) { firebaseId }.test()

        testObserver
            .assertNoErrors()

        Mockito.verify(preferences).getSetupId()
        Mockito.verify(preferences).setSetupId(setupId)
        Mockito.verify(preferences).setAccessToken(token)
    }

}
