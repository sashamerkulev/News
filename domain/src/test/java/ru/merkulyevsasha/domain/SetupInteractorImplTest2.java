package ru.merkulyevsasha.domain;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.UUID;

import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import kotlin.jvm.JvmField;
import ru.merkulyevsasha.core.domain.SetupInteractor;
import ru.merkulyevsasha.core.models.Token;
import ru.merkulyevsasha.core.preferences.KeyValueStorage;
import ru.merkulyevsasha.core.repositories.DatabaseRepository;
import ru.merkulyevsasha.core.repositories.SetupApiRepository;

public class SetupInteractorImplTest2 {

    @Rule
    @JvmField
    public RxSchedulerRule2 schedulerRule = new RxSchedulerRule2();

    private KeyValueStorage preferences = Mockito.mock(KeyValueStorage.class);
    private DatabaseRepository databaseRepository = Mockito.mock(DatabaseRepository.class);
    private SetupApiRepository setupApiRepository = Mockito.mock(SetupApiRepository.class);

    private SetupInteractor setupInteractor = new SetupInteractorImpl(preferences, setupApiRepository, databaseRepository);

    @After
    public void tearDown() {
        Mockito.verifyNoMoreInteractions(preferences, databaseRepository, setupApiRepository);
    }

    @Test
    public void registerSetup_calls_repo_registerSetup_if_setup_id_is_empty() {
        String setupId = UUID.randomUUID().toString();
        String token = UUID.randomUUID().toString();
        String firebaseId = UUID.randomUUID().toString();

        Mockito.when(preferences.getSetupId()).thenReturn("");
        Mockito.when(setupApiRepository.registerSetup(setupId, Mockito.anyString())).thenReturn(Single.just(new Token(token)));

        TestObserver testObserver = setupInteractor.registerSetup(setupId, () -> firebaseId).test();

        testObserver
            .assertNoErrors()
            .assertComplete();

        Mockito.verify(preferences).getSetupId();
        Mockito.verify(preferences).setSetupId(setupId);
        Mockito.verify(preferences).setAccessToken(token);
    }

}
