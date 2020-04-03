package com.github.sikv.photos.ui.navigation

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment

class NavigationDispatcher(
        private val fragment: Fragment,
        @IdRes private val containerId: Int
) : Navigation {

    override fun addFragment(fragment: Fragment) {
        this.fragment.childFragmentManager.beginTransaction()
                .replace(containerId, fragment)
                .addToBackStack(null)
                .commit()
    }

    override fun backPressed(): Boolean {
        val fragmentManager = fragment.childFragmentManager

        return if (fragmentManager.backStackEntryCount > 1) {
            fragmentManager.popBackStack()
            true
        } else {
            false
        }
    }

    override fun backToRoot(): Fragment? {
        val fragmentManager = fragment.childFragmentManager

        for (i in 1 until fragmentManager.backStackEntryCount) {
            fragmentManager.popBackStack()
        }

        return fragmentManager.fragments.firstOrNull()
    }
}