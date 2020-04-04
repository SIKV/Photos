package com.github.sikv.photos.ui.navigation

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import com.github.sikv.photos.R
import com.github.sikv.photos.util.hideSoftInput

class NavigationDispatcher(
        private val fragment: Fragment,
        @IdRes private val containerId: Int
) : Navigation {

    override fun addFragment(fragment: Fragment, withAnimation: Boolean) {
        val transaction = this.fragment.childFragmentManager.beginTransaction()

        if (withAnimation) {
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
        }

        transaction.add(containerId, fragment)
                .addToBackStack(null)
                .commit()
    }

    override fun backPressed(): Boolean {
        fragment.activity?.hideSoftInput()

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