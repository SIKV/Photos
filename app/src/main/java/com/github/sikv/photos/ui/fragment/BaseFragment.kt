package com.github.sikv.photos.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.github.sikv.photos.ui.custom.toolbar.FragmentToolbar
import com.github.sikv.photos.ui.custom.toolbar.FragmentToolbarManager


abstract class BaseFragment : Fragment() {

    private var toolbar: FragmentToolbar? = null
    private var fragmentToolbarManager: FragmentToolbarManager? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = onCreateToolbar()

        toolbar?.let { toolbar ->
            fragmentToolbarManager = FragmentToolbarManager(toolbar, view)
            fragmentToolbarManager?.prepareToolbar()
        }
    }

    protected open fun onCreateToolbar(): FragmentToolbar? {
        return null
    }

    protected fun setMenuItemVisibility(menuItem: Int, visible: Boolean) {
        fragmentToolbarManager?.setMenuItemVisibility(menuItem, visible)
    }
}