package ru.merkulyevsasha.news.presentation.main

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import dagger.hilt.android.AndroidEntryPoint
import ru.merkulyevsasha.core.models.ThemeEnum
import ru.merkulyevsasha.core.presentation.OnThemeChangedCallback
import ru.merkulyevsasha.coreandroid.common.ToolbarCombinator
import ru.merkulyevsasha.coreandroid.common.observe
import ru.merkulyevsasha.main.presentation.MainActivityViewModel
import ru.merkulyevsasha.news.R
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ToolbarCombinator, OnThemeChangedCallback {

    @Inject
    lateinit var model: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            model.onSetup()
        }
        observeOnThemeChanged()
        observeOnErrorsChanged()
    }

    override fun onBackPressed() {
        if (isMainFragmentActive()) {
            finish()
        } else {
            super.onBackPressed()
        }
    }

    override fun bindToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
    }

    override fun unbindToolbar() {
        setSupportActionBar(null)
    }

    override fun onThemeChanged(theme: ThemeEnum) {
        when (theme) {
            ThemeEnum.ClassicNight -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
            ThemeEnum.Classic -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
        }
    }

    private fun observeOnThemeChanged() {
        observe(model.themes) { theme ->
            onThemeChanged(theme)
        }
    }

    private fun observeOnErrorsChanged() {
        observe(model.messages) { error ->
            showFatalError(error)
        }
    }

    private fun showFatalError(err: String) {
        Toast.makeText(this, err, Toast.LENGTH_LONG).show()
        finish()
    }

    private fun isMainFragmentActive(): Boolean {
        for (fragment in supportFragmentManager.fragments) {
            if (fragment is MainFragment && fragment.isVisible) {
                return true
            }
        }
        return false
    }
}
