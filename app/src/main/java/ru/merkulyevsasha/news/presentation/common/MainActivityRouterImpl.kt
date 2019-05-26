package ru.merkulyevsasha.news.presentation.common

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import ru.merkulyevsasha.core.routers.MainActivityRouter
import ru.merkulyevsasha.news.R
import ru.merkulyevsasha.news.presentation.articles.ArticlesFragment
import ru.merkulyevsasha.news.presentation.useractivities.UserActivitiesFragment
import ru.merkulyevsasha.news.presentation.userinfo.UserInfoFragment

class MainActivityRouterImpl(
    private val fragmentManager: FragmentManager
) : MainActivityRouter {

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

    private fun findOrCreateFragment(tag: String, createFragment: () -> Fragment): Fragment {
        return fragmentManager.findFragmentByTag(tag) ?: createFragment()
    }

    private fun replaceFragment(tag: String, fragment: Fragment) {
        fragmentManager.beginTransaction()
            .replace(R.id.container, fragment, tag)
            .addToBackStack(tag)
            .commit()
    }

}