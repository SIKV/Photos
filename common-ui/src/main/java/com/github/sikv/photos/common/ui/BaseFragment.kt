package com.github.sikv.photos.common.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.sikv.photos.common.ui.toolbar.FragmentToolbar
import com.github.sikv.photos.common.ui.toolbar.FragmentToolbarManager
import com.google.android.material.color.MaterialColors

@Deprecated("Use Jetpack Compose")
abstract class BaseFragment : Fragment() {

    private var toolbar: FragmentToolbar? = null
    private var fragmentToolbarManager: FragmentToolbarManager? = null

    protected open val overrideBackground = false

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
