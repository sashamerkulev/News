package ru.merkulyevsasha.news.presentation.splash

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import ru.merkulyevsasha.RequireServiceLocator
import ru.merkulyevsasha.core.ServiceLocator
import ru.merkulyevsasha.core.domain.SetupInteractor
import ru.merkulyevsasha.news.R
import java.util.*

class SplashActivity : AppCompatActivity(), SplashView, RequireServiceLocator {

    private var presenter: SplashPresenterImpl? = null
    private lateinit var serviceLocator: ServiceLocator

    override fun setServiceLocator(serviceLocator: ServiceLocator) {
        this.serviceLocator = serviceLocator
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerNewsChannel()

        val setupInteractor = serviceLocator.get(SetupInteractor::class.java)
        presenter = SplashPresenterImpl(setupInteractor)
    }

    override fun showMainScreen() {
//        serviceLocator.getApplicationRouter().showMainActivity()
        finish()
    }

    override fun showFatalError() {
        Toast.makeText(this, "Oops!", Toast.LENGTH_LONG).show()
        finish()
    }

    override fun onResume() {
        super.onResume()
        presenter?.bindView(this)
        presenter?.onSetup(getFirebaseId = { UUID.randomUUID().toString() })
    }

    override fun onPause() {
        presenter?.unbindView()
        super.onPause()
    }

    override fun onDestroy() {
        presenter?.onDestroy()
        presenter = null
        serviceLocator.releaseAll()
        super.onDestroy()
    }

    private fun registerNewsChannel() {
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
