package com.github.sikv.photos.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.sikv.photos.R
import com.github.sikv.photos.ui.custom.toolbar.FragmentToolbar
import com.github.sikv.photos.ui.custom.toolbar.FragmentToolbarManager
import com.github.sikv.photos.ui.navigation.Navigation
import com.github.sikv.photos.ui.navigation.NavigationProvider

abstract class BaseFragment : Fragment() {

    private var toolbar: FragmentToolbar? = null
    private var fragmentToolbarManager: FragmentToolbarManager? = null

    protected open val overrideBackground = false

    var navigation: Navigation? = null
        private set

    override fun onAttach(context: Context) {
        super.onAttach(context)

        navigation = (parentFragment as? NavigationProvider)?.provideNavigation()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (overrideBackground) {
            context?.let {
                view.setBackgroundColor(ContextCompat.getColor(it, R.color.colorBackground))
            }

            view.isClickable = true
            view.isFocusable = true
        }

        toolbar = onCreateToolbar()

        toolbar?.let { toolbar ->
            fragmentToolbarManager = FragmentToolbarManager(toolbar, view)
            fragmentToolbarManager?.prepareToolbar()
        }
    }

    override fun onDetach() {
        navigation = null

        super.onDetach()
    }

    open fun onScrollToTop() { }

    protected open fun onCreateToolbar(): FragmentToolbar? {
        return null
    }

    protected fun setMenuItemVisibility(menuItem: Int, visible: Boolean) {
        fragmentToolbarManager?.setMenuItemVisibility(menuItem, visible)
    }
}