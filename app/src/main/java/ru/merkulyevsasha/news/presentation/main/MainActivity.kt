package ru.merkulyevsasha.news.presentation.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import ru.merkulyevsasha.RequireServiceLocator
import ru.merkulyevsasha.apprate.AppRateRequester
import ru.merkulyevsasha.core.ServiceLocator
import ru.merkulyevsasha.core.domain.SetupInteractor
import ru.merkulyevsasha.core.routers.MainActivityRouter
import ru.merkulyevsasha.news.BuildConfig
import ru.merkulyevsasha.news.R
import ru.merkulyevsasha.news.presentation.common.ToolbarCombinator
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class MainActivity : AppCompatActivity(),
    MainView, ToolbarCombinator, RequireServiceLocator {

    companion object {
        @JvmStatic
        fun show(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }

    private lateinit var serviceLocator: ServiceLocator
    private lateinit var mainActivityRouter: MainActivityRouter
    private lateinit var presenter: MainPresenter
    private val backButtonState = AtomicBoolean(false)

    override fun setServiceLocator(serviceLocator: ServiceLocator) {
        this.serviceLocator = serviceLocator
        mainActivityRouter = serviceLocator.get(MainActivityRouter::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_Normal)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val interactor = serviceLocator.get(SetupInteractor::class.java)
        presenter = MainPresenter(interactor)

        AppRateRequester.run(this, BuildConfig.APPLICATION_ID)

        //FirebaseInstanceId.getInstance().getToken("", "")
        if (savedInstanceState == null) {
            presenter.bindView(this)
            presenter.onSetup(getFirebaseId = {
                UUID.randomUUID().toString()
            })
        }
    }

    override fun onDestroy() {
        presenter.unbindView()
        presenter.onDestroy()
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.fragments.size <= 1) {
            finish()
        } else {
            super.onBackPressed()
        }
    }

    override fun showMainScreen() {
        serviceLocator.get(MainActivityRouter::class.java).showMain()
    }

    override fun showFatalError() {
        serviceLocator.get(MainActivityRouter::class.java).showMain()
    }

    override fun bindToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
    }

    override fun unbindToolbar() {
        setSupportActionBar(null)
    }
}
