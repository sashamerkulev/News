package ru.merkulyevsasha.news.presentation.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import dagger.hilt.android.AndroidEntryPoint
import ru.merkulyevsasha.core.models.ThemeEnum
import ru.merkulyevsasha.core.preferences.KeyValueStorage
import ru.merkulyevsasha.core.presentation.OnThemeChangedCallback
import ru.merkulyevsasha.coreandroid.common.ToolbarCombinator
import ru.merkulyevsasha.main.MainPresenterImpl
import ru.merkulyevsasha.main.MainView
import ru.merkulyevsasha.news.R
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(),
    MainView, ToolbarCombinator, OnThemeChangedCallback {

    companion object {
        @JvmStatic
        fun show(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }

    @Inject
    lateinit var presenter: MainPresenterImpl
    @Inject
    lateinit var keyValueStorage: KeyValueStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

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

    override fun onPause() {
        presenter.unbindView()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        presenter.bindView(this)
    }

    override fun onDestroy() {
        presenter.unbindView()
        presenter.onDestroy()
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (isMainFragmentActive()) {
            finish()
        } else {
            super.onBackPressed()
        }
    }

    override fun changeTheme(theme: ThemeEnum) {
        onThemeChanged(theme)
    }

    override fun showFatalError(err: String) {
        Toast.makeText(this, err, Toast.LENGTH_LONG).show()
        //Toast.makeText(this, getString(R.string.first_loading_error_message), Toast.LENGTH_LONG).show()
        finish()
    }

    override fun bindToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
    }

    override fun unbindToolbar() {
        setSupportActionBar(null)
    }

    private fun isMainFragmentActive(): Boolean {
        for (fff in supportFragmentManager.fragments) {
            if (fff is MainFragment && fff.isVisible) {
                return true
            }
        }
        return false
    }

    override fun onThemeChanged(theme: ThemeEnum) {
        when (theme) {
            ThemeEnum.ClassicNight -> delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            ThemeEnum.Classic -> delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
