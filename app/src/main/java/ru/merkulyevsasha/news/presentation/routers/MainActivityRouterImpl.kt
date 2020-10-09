package ru.merkulyevsasha.news.presentation.routers

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import ru.merkulyevsasha.articlecomments.presentation.ArticleCommentsFragment
import ru.merkulyevsasha.articledetails.presentation.ArticleDetailsFragment
import ru.merkulyevsasha.articles.presentation.ArticlesFragment
import ru.merkulyevsasha.core.Logger
import ru.merkulyevsasha.core.routers.MainActivityRouter
import ru.merkulyevsasha.news.R
import ru.merkulyevsasha.news.presentation.main.MainFragment
import ru.merkulyevsasha.sourcearticles.presentation.SourceArticlesFragment
import ru.merkulyevsasha.sourcelist.presentation.SourceListFragment
import ru.merkulyevsasha.useractivities.presentation.UserActivitiesFragment
import ru.merkulyevsasha.userinfo.presentation.UserInfoFragment

class MainActivityRouterImpl(
    private val fragmentManager: FragmentManager,
    private val l: Logger
) : MainActivityRouter {
    companion object {
        private const val TAG = "MainActivityRouter"
    }

    override fun showMainFragment() {
        val tag = MainFragment.TAG
        val fragment = findOrCreateFragment(tag) { MainFragment.newInstance() }
        replaceFragment(R.id.container, tag, fragment)
    }

    override fun showArticleDetails(articleId: Int) {
        l.i(TAG, "showArticleDetails($articleId)")
        val tag = ArticleDetailsFragment.TAG
        val fragment = findOrCreateFragment(tag) { ArticleDetailsFragment.newInstance(articleId) }
        replaceFragment(R.id.container, tag, fragment, true)
    }

    override fun showArticleComments(articleId: Int) {
        val tag = ArticleCommentsFragment.TAG
        val fragment = findOrCreateFragment(tag) { ArticleCommentsFragment.newInstance(articleId) }
        replaceFragment(R.id.container, tag, fragment, true)
    }

    override fun showSourceArticles(sourceId: String, sourceName: String) {
        val tag = SourceArticlesFragment.TAG
        val fragment = findOrCreateFragment(tag) { SourceArticlesFragment.newInstance(sourceId, sourceName) }
        replaceFragment(R.id.container, tag, fragment, true)
    }

    override fun showArticles() {
        val tag = ArticlesFragment.TAG
        val fragment = findOrCreateFragment(tag) { ArticlesFragment.newInstance() }
        replaceFragment(R.id.mainContainer, tag, fragment)
    }

    override fun showSourceList() {
        val tag = SourceListFragment.TAG
        val fragment = findOrCreateFragment(tag) { SourceListFragment.newInstance() }
        replaceFragment(R.id.mainContainer, tag, fragment)
    }

    override fun showUserActivities() {
        val tag = UserActivitiesFragment.TAG
        val fragment = findOrCreateFragment(tag) { UserActivitiesFragment.newInstance() }
        replaceFragment(R.id.mainContainer, tag, fragment)
    }

    override fun showUserInfo() {
        val tag = UserInfoFragment.TAG
        val fragment = findOrCreateFragment(tag) { UserInfoFragment.newInstance() }
        replaceFragment(R.id.mainContainer, tag, fragment)
    }

    private fun findOrCreateFragment(tag: String, createFragment: () -> Fragment): Fragment {
        return fragmentManager.findFragmentByTag(tag) ?: createFragment()
    }

    private fun replaceFragment(containerId: Int, tag: String, fragment: Fragment, addToBackStack: Boolean = false) {
        val fragmentTransaction = fragmentManager.beginTransaction()
            .replace(containerId, fragment, tag)
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(tag)
        }
        fragmentTransaction.commitAllowingStateLoss()
    }

}