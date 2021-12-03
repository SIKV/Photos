package com.github.sikv.photos.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.sikv.photos.R
import com.github.sikv.photos.ui.custom.toolbar.FragmentToolbar
import com.github.sikv.photos.ui.custom.toolbar.FragmentToolbarManager
import com.github.sikv.photos.ui.navigation.Navigation
import com.github.sikv.photos.ui.navigation.NavigationProvider
import com.google.android.material.color.MaterialColors

abstract class BaseFragment : Fragment() {

    private var toolbar: FragmentToolbar? = null
    private var fragmentToolbarManager: FragmentToolbarManager? = null

    protected open val overrideBackground = false

    val navigation: Navigation?
        get() {
            var parent = parentFragment

            while (parent != null) {
                if (parent is NavigationProvider) {
                    return (parent as NavigationProvider).provideNavigation()
                } else {
                    parent = parent.parentFragment
                }
            }
            return null
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (overrideBackground) {
            val backgroundColor =
                MaterialColors.getColor(requireContext(), R.attr.colorSurface, Color.BLACK)
            view.setBackgroundColor(backgroundColor)

            view.isClickable = true
            view.isFocusable = true
        }

        toolbar = onCreateToolbar()

        toolbar?.let { toolbar ->
            fragmentToolbarManager = FragmentToolbarManager(toolbar, view)
            fragmentToolbarManager?.prepareToolbar()
        }
    }

    open fun onScrollToTop() {}

    protected open fun onCreateToolbar(): FragmentToolbar? {
        return null
    }

    protected fun setMenuItemVisibility(menuItem: Int, visible: Boolean) {
        fragmentToolbarManager?.setMenuItemVisibility(menuItem, visible)
    }

    protected fun showMessage(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT)
            .show()
    }
}
