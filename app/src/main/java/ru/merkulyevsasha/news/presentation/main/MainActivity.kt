package ru.merkulyevsasha.news.presentation.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import ru.merkulyevsasha.RequireServiceLocator
import ru.merkulyevsasha.core.ServiceLocator
import ru.merkulyevsasha.core.domain.SetupInteractor
import ru.merkulyevsasha.core.routers.MainActivityRouter
import ru.merkulyevsasha.news.R
import ru.merkulyevsasha.news.presentation.common.ToolbarCombinator

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

        //AppRateRequester.run(this, BuildConfig.APPLICATION_ID)

        if (savedInstanceState == null) {
            presenter.bindView(this)
            presenter.onSetup()

//            FirebaseInstanceId.getInstance().instanceId
//                .addOnCompleteListener(OnCompleteListener { task ->
//                    if (!task.isSuccessful) {
//                        return@OnCompleteListener
//                    }
//                    // Get new Instance ID token
//                    val token = task.result?.token
//                    // Log and toast
//                    token?.let {
//                        presenter.onUpdateFirebaseId(it)
//                    }
//                })
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
        Toast.makeText(this, getString(R.string.first_loading_error_message), Toast.LENGTH_LONG).show()
        finish()
    }

    override fun bindToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
    }

    override fun unbindToolbar() {
        setSupportActionBar(null)
    }
}
