package ru.merkulyevsasha.news.presentation.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.bottomNav
import kotlinx.android.synthetic.main.activity_main.cardview
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
import ru.merkulyevsasha.news.presentation.common.ShowActionBarListener
import ru.merkulyevsasha.news.presentation.common.ToolbarCombinator
import ru.merkulyevsasha.news.presentation.useractivities.UserActivitiesFragment
import ru.merkulyevsasha.news.presentation.userinfo.UserInfoFragment

class MainActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener, MainView,
    ToolbarCombinator, ShowActionBarListener, MainActivityRouter {

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

    override fun onBackPressed() {
//        if (supportFragmentManager.fragments.size <= 1) {
        finish()
//        } else {
//            super.onBackPressed()
//        }
    }

    override fun combine(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(this, drawer, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onShowActionBar(show: Boolean) {
        if (show) {
            supportActionBar?.show()
            cardview.visibility = View.VISIBLE
        } else {
            supportActionBar?.hide()
            cardview.visibility = View.GONE
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun showArticles() {
        val tag = ArticlesFragment.TAG
        val fragment = findOrCreateFragment(tag) { ArticlesFragment.newInstance() }
        replaceFragment(tag, fragment)
    }

    override fun showUserActivities() {
        val tag = UserActivitiesFragment.TAG
        val fragment = findOrCreateFragment(tag) { UserActivitiesFragment.newInstance() }
        replaceFragment(tag, fragment)
    }

    override fun showUserInfo() {
        val tag = UserInfoFragment.TAG
        val fragment = findOrCreateFragment(tag) { UserInfoFragment.newInstance() }
        replaceFragment(tag, fragment)
    }

    override fun showArticleDetails(articleId: Int) {
        ArticleDetailsActivity.show(this, articleId)
    }

    private fun findOrCreateFragment(tag: String, createFragment: () -> Fragment): Fragment {
        return supportFragmentManager.findFragmentByTag(tag) ?: createFragment()
    }

    private fun replaceFragment(tag: String, fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment, tag)
            .addToBackStack(tag)
            .commit()
    }
}
