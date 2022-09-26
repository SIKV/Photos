package com.github.sikv.photos.navigation

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

private fun Activity.hideSoftInput() {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
}

private fun FragmentManager.getActiveFragment(): Fragment? {
    fragments.forEach { fragment ->
        if (fragment.isVisible) {
            return fragment
        }
    }
    return null
}

class NavigationDispatcher(
    private val fragment: Fragment,
    @IdRes private val containerId: Int
) : Navigation {

    private var destinationChangedListener: OnDestinationChangedListener? = null
    private var backPressedListener: OnBackPressedListener? = null

    override fun addFragment(fragment: Fragment, animation: NavigationAnimation) {
        val fm = this.fragment.childFragmentManager
        val transaction = fm.beginTransaction()

        when (animation) {
            NavigationAnimation.SLIDE_H -> {
                transaction.setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
            }
            NavigationAnimation.SLIDE_V -> {
                transaction.setCustomAnimations(
                    R.anim.slide_in_bottom,
                    R.anim.slide_out_top,
                    R.anim.slide_in_bottom,
                    R.anim.slide_out_top
                )
            }
            else -> { }
        }

        transaction
            .add(containerId, fragment)
            .addToBackStack(null)
            .commit()

        destinationChangedListener?.onDestinationChanged(fragment)
    }

    override fun backPressed(): Boolean {
        fragment.activity?.hideSoftInput()

        val fm = fragment.childFragmentManager

        return if (fm.backStackEntryCount > 1) {
            fm.popBackStack()

            destinationChangedListener?.onDestinationChanged(fm.getActiveFragment())
            backPressedListener?.onBackPressed()
            true
        } else {
            false
        }
    }

    override fun backToRoot(): Fragment? {
        val fm = fragment.childFragmentManager

        for (i in 1 until fm.backStackEntryCount) {
            fm.popBackStack()
        }

        val rootFragment = fm.fragments.firstOrNull()

        destinationChangedListener?.onDestinationChanged(rootFragment)
        backPressedListener?.onBackPressed()

        return rootFragment
    }

    override fun setOnDestinationChangedListener(destinationChangedListener: OnDestinationChangedListener?) {
        this.destinationChangedListener = destinationChangedListener
    }

    override fun setOnBackPressedListener(backPressedListener: OnBackPressedListener?) {
        this.backPressedListener = backPressedListener
    }
}
