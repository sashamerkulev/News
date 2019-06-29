package ru.merkulyevsasha.news.fcm

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ru.merkulyevsasha.ServiceLocatorImpl
import ru.merkulyevsasha.core.domain.SetupInteractor
import timber.log.Timber

class FcmService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage?) {
    }

    override fun onNewToken(token: String?) {
        val serviceLocator = ServiceLocatorImpl.getInstance(applicationContext, null)
        val setupInteractor = serviceLocator.get(SetupInteractor::class.java)
        token?.let {
            setupInteractor.updateFirebaseToken(token)
                .subscribe({}, { Timber.e(it) })
        }
    }
}