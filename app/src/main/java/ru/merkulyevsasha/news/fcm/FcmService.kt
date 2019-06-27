package ru.merkulyevsasha.news.fcm

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FcmService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage?) {

    }

    override fun onNewToken(token: String?) {

    }
}