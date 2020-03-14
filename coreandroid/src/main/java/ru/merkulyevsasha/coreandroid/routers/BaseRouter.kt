package ru.merkulyevsasha.coreandroid.routers

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import ru.merkulyevsasha.core.Logger

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
        fragmentTransaction.commitAllowingStateLoss()
        Logger.log("BaseRouter tag -> $fragment Fragment-> $fragment")
    }

}