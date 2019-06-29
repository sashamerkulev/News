package ru.merkulyevsasha.news.presentation.routers

import androidx.fragment.app.FragmentManager
import ru.merkulyevsasha.core.routers.MainFragmentRouter
import ru.merkulyevsasha.news.R
import ru.merkulyevsasha.news.presentation.articles.ArticlesFragment
import ru.merkulyevsasha.news.presentation.useractivities.UserActivitiesFragment
import ru.merkulyevsasha.news.presentation.userinfo.UserInfoFragment

class MainFragmentRouterImpl(fragmentManager: FragmentManager) : BaseRouter(R.id.mainContainer, fragmentManager), MainFragmentRouter {

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

}