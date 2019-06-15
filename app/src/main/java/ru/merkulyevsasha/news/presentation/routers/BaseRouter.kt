package ru.merkulyevsasha.news.presentation.routers

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager

open class BaseRouter(private val containerId: Int, private val fragmentManager: FragmentManager) {

    fun findOrCreateFragment(tag: String, createFragment: () -> Fragment): Fragment {
        return fragmentManager.findFragmentByTag(tag) ?: createFragment()
    }

    fun replaceFragment(tag: String, fragment: Fragment, addToBackStack: Boolean = false) {
        val fragmentTransaction = fragmentManager.beginTransaction()
            .replace(containerId, fragment, tag)
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(tag)
        }
        fragmentTransaction.commit()
    }

}