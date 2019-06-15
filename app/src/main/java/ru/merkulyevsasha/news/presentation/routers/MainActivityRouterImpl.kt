package ru.merkulyevsasha.news.presentation.routers

import android.support.v4.app.FragmentManager
import ru.merkulyevsasha.core.routers.MainActivityRouter
import ru.merkulyevsasha.news.R
import ru.merkulyevsasha.news.presentation.articlecomments.ArticleCommentsFragment
import ru.merkulyevsasha.news.presentation.articledetails.ArticleDetailsFragment
import ru.merkulyevsasha.news.presentation.main.MainFragment
import ru.merkulyevsasha.news.presentation.splash.SplashFragment

class MainActivityRouterImpl(fragmentManager: FragmentManager) : BaseRouter(R.id.container, fragmentManager), MainActivityRouter {

    override fun showMain() {
        val tag = MainFragment.TAG
        val fragment = findOrCreateFragment(tag) { MainFragment.newInstance() }
        replaceFragment(tag, fragment)
    }

    override fun showSplash() {
        val tag = SplashFragment.TAG
        val fragment = findOrCreateFragment(tag) { SplashFragment.newInstance() }
        replaceFragment(tag, fragment, true)
    }

    override fun showArticleDetails(articleId: Int) {
        val tag = ArticleDetailsFragment.TAG
        val fragment = findOrCreateFragment(tag) { ArticleDetailsFragment.newInstance(articleId) }
        replaceFragment(tag, fragment, true)
    }

    override fun showArticleComments(articleId: Int) {
        val tag = ArticleCommentsFragment.TAG
        val fragment = findOrCreateFragment(tag) { ArticleCommentsFragment.newInstance(articleId) }
        replaceFragment(tag, fragment, true)
    }

}