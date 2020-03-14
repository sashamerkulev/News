package ru.merkulyevsasha.news.presentation.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_main.bottomNav
import ru.merkulyevsasha.articles.ArticlesFragment
import ru.merkulyevsasha.core.RequireServiceLocator
import ru.merkulyevsasha.core.ServiceLocator
import ru.merkulyevsasha.core.routers.MainFragmentRouter
import ru.merkulyevsasha.news.R
import ru.merkulyevsasha.sourcelist.SourceListFragment
import ru.merkulyevsasha.useractivities.UserActivitiesFragment
import ru.merkulyevsasha.userinfo.UserInfoFragment

class MainFragment : Fragment(), RequireServiceLocator {

    companion object {
        @JvmStatic
        val TAG: String = "MainFragment"
        private val KEY_FRAG = "KEY_FRAG"

        @JvmStatic
        fun newInstance(): Fragment {
            val fragment = MainFragment()
            fragment.arguments = Bundle()
            return fragment
        }
    }

    private lateinit var mainFragmentRouter: MainFragmentRouter
    private var currentFrag = ArticlesFragment.TAG

    private val navigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_articles -> {
                    currentFrag = ArticlesFragment.TAG
                    mainFragmentRouter.showArticles()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_source_articles -> {
                    currentFrag = SourceListFragment.TAG
                    mainFragmentRouter.showSourceList()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_actions -> {
                    currentFrag = UserActivitiesFragment.TAG
                    mainFragmentRouter.showUserActivities()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_user -> {
                    currentFrag = UserInfoFragment.TAG
                    mainFragmentRouter.showUserInfo()
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    override fun setServiceLocator(serviceLocator: ServiceLocator) {
        mainFragmentRouter = serviceLocator.get(MainFragmentRouter::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_main, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomNav.setOnNavigationItemSelectedListener(navigationItemSelectedListener)

        if (savedInstanceState == null) {
            when(currentFrag) {
                ArticlesFragment.TAG -> mainFragmentRouter.showArticles()
                SourceListFragment.TAG -> mainFragmentRouter.showSourceList()
                UserActivitiesFragment.TAG -> mainFragmentRouter.showUserActivities()
                UserInfoFragment.TAG -> mainFragmentRouter.showUserInfo()
            }
        } else {
            currentFrag = savedInstanceState.getString(KEY_FRAG) ?: ArticlesFragment.TAG
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_FRAG, currentFrag)
    }
}