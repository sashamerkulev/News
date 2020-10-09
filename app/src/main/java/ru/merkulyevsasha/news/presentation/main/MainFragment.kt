package ru.merkulyevsasha.news.presentation.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_main.bottomNav
import ru.merkulyevsasha.articles.presentation.ArticlesFragment
import ru.merkulyevsasha.core.routers.MainActivityRouter
import ru.merkulyevsasha.news.R
import ru.merkulyevsasha.sourcelist.presentation.SourceListFragment
import ru.merkulyevsasha.useractivities.presentation.UserActivitiesFragment
import ru.merkulyevsasha.userinfo.presentation.UserInfoFragment
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment() {

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

    @Inject
    lateinit var router: MainActivityRouter
    private var currentFrag = ArticlesFragment.TAG

    private val navigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_articles -> {
                    currentFrag = ArticlesFragment.TAG
                    router.showArticles()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_source_articles -> {
                    currentFrag = SourceListFragment.TAG
                    router.showSourceList()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_actions -> {
                    currentFrag = UserActivitiesFragment.TAG
                    router.showUserActivities()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_user -> {
                    currentFrag = UserInfoFragment.TAG
                    router.showUserInfo()
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_main, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomNav.setOnNavigationItemSelectedListener(navigationItemSelectedListener)

        if (savedInstanceState == null) {
            when (currentFrag) {
                ArticlesFragment.TAG -> router.showArticles()
                SourceListFragment.TAG -> router.showSourceList()
                UserActivitiesFragment.TAG -> router.showUserActivities()
                UserInfoFragment.TAG -> router.showUserInfo()
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