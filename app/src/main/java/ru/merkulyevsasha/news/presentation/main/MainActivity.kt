package ru.merkulyevsasha.news.presentation.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.bottomNav
import kotlinx.android.synthetic.main.activity_main.drawer
import kotlinx.android.synthetic.main.activity_main.navigation
import ru.merkulyevsasha.apprate.AppRateRequester
import ru.merkulyevsasha.core.domain.SetupInteractor
import ru.merkulyevsasha.news.BuildConfig
import ru.merkulyevsasha.news.NewsApp
import ru.merkulyevsasha.news.R
import ru.merkulyevsasha.news.presentation.articledetails.ArticleDetailsActivity
import ru.merkulyevsasha.news.presentation.articles.ArticlesFragment
import ru.merkulyevsasha.news.presentation.common.MainActivityRouter
import ru.merkulyevsasha.news.presentation.common.ToolbarCombinator
import ru.merkulyevsasha.news.presentation.useractivities.UserActivitiesFragment
import ru.merkulyevsasha.news.presentation.userinfo.UserInfoFragment

class MainActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener, MainView, ToolbarCombinator, MainActivityRouter {

    companion object {
        @JvmStatic
        fun show(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }

    private lateinit var presenter: MainPresenter

    private val navigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            ru.merkulyevsasha.news.R.id.navigation_articles -> {
                showArticles()
                return@OnNavigationItemSelectedListener true
            }
            ru.merkulyevsasha.news.R.id.navigation_actions -> {
                showUserActivities()
                return@OnNavigationItemSelectedListener true
            }
            ru.merkulyevsasha.news.R.id.navigation_user -> {
                showUserInfo()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_Normal)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        navigation.setNavigationItemSelectedListener(this)

        val serviceLocator = (application as NewsApp).getServiceLocator()
        val interactor = serviceLocator.get(SetupInteractor::class.java)
        presenter = MainPresenter(interactor)

        AppRateRequester.Run(this, BuildConfig.APPLICATION_ID)

        bottomNav.setOnNavigationItemSelectedListener(navigationItemSelectedListener)

//        val adRequestBuilder = AdRequest.Builder()
//        if (BuildConfig.DEBUG_MODE) {
//            adRequestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//        }
//        adRequestBuilder.addTestDevice("349C53FFD0654BDC5FF7D3D9254FC8E6").build()
//        adView.loadAd(adRequestBuilder.build())

        if (savedInstanceState == null) {
            showArticles()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onPause() {
//        if (adView != null) {
//            adView.pause()
//        }
        presenter.unbindView()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
//        adView.resume()
        presenter.bindView(this)
    }

    override fun onDestroy() {
//        if (adView != null) {
//            adView.destroy()
//        }
        presenter.onDestroy()
        super.onDestroy()
    }

    override fun combine(toolbar: Toolbar) {
        val toggle = ActionBarDrawerToggle(this, drawer, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun showArticles() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, ArticlesFragment.newInstance(), ArticlesFragment.TAG)
            .commit()
    }

    override fun showUserActivities() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, UserActivitiesFragment.newInstance(), UserActivitiesFragment.TAG)
            .commit()
    }

    override fun showUserInfo() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, UserInfoFragment.newInstance(), UserInfoFragment.TAG)
            .commit()
    }

    override fun showArticleDetails(articleId: Int) {
        ArticleDetailsActivity.show(this, articleId)
    }
}
