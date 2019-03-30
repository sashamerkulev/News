package ru.merkulyevsasha.news.presentation.main

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.activity_main.drawer_layout
import kotlinx.android.synthetic.main.activity_main.nav_view
import kotlinx.android.synthetic.main.app_bar_main.adView
import kotlinx.android.synthetic.main.app_bar_main.collapsinng_toolbar_layout
import kotlinx.android.synthetic.main.app_bar_main.toolbar
import ru.merkulyevsasha.apprate.AppRateRequester
import ru.merkulyevsasha.news.BuildConfig
import ru.merkulyevsasha.news.R

class MainActivity : AppCompatActivity(), MainView, NavigationView.OnNavigationItemSelectedListener {

    private lateinit var pres: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pres = MainPresenter()

        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        collapsinng_toolbar_layout.isTitleEnabled = false

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        AppRateRequester.Run(this, BuildConfig.APPLICATION_ID)

        val adRequest = if (BuildConfig.DEBUG_MODE)
            AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice("349C53FFD0654BDC5FF7D3D9254FC8E6").build()
        else
            AdRequest.Builder().addTestDevice("349C53FFD0654BDC5FF7D3D9254FC8E6").build()
        adView.loadAd(adRequest)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
    }

    public override fun onPause() {
        if (adView != null) {
            adView.pause()
        }
        pres.unbindView()
        super.onPause()
    }

    public override fun onResume() {
        super.onResume()
        adView.resume()
        pres.bindView(this)
    }

    public override fun onDestroy() {
        if (adView != null) {
            adView.destroy()
        }
        pres.onDestroy()
        super.onDestroy()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

}


