package ru.merkulyevsasha.news.presentation.splash

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import ru.merkulyevsasha.core.domain.SetupInteractor
import ru.merkulyevsasha.news.NewsApp
import ru.merkulyevsasha.news.R
import ru.merkulyevsasha.news.presentation.main.MainActivity
import java.util.*

class SplashActivity : AppCompatActivity(), SplashView {

    private lateinit var presenter: SplashPresenterImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerNewsChannel()

        val serviceLocator = (application as NewsApp).getServiceLocator()
        val setupInteractor = serviceLocator.get(SetupInteractor::class.java)
        presenter = SplashPresenterImpl(setupInteractor)
    }

    override fun showMainScreen() {
        MainActivity.show(this)
        finish()
    }

    override fun showFatalError() {
        Toast.makeText(this, "Oops!", Toast.LENGTH_LONG).show()
    }

    override fun onResume() {
        super.onResume()
        presenter.bindView(this)
        val setupId = UUID.randomUUID().toString()
        presenter.onSetup(setupId, getFirebaseId = { UUID.randomUUID().toString() })
    }

    override fun onPause() {
        presenter.unbindView()
        super.onPause()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    private fun registerNewsChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        // Creating an existing notification channel with its original values performs no operation, so it's safe to call this code when starting an app.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.download_channel_name)
            val description = getString(R.string.download_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(getString(R.string.notification_channell_id), name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }
}
