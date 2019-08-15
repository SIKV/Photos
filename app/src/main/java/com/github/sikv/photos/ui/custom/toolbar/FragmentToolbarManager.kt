package com.github.sikv.photos.ui.custom.toolbar

import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar

class FragmentToolbarManager constructor(
        private var builder: FragmentToolbar,
        private var container: View) {

    private var toolbar: Toolbar? = null

    fun prepareToolbar() {
        if (builder.resId != FragmentToolbar.NO_TOOLBAR) {
            toolbar = container.findViewById(builder.resId)


            if (builder.menuId != -1) {
                toolbar?.inflateMenu(builder.menuId)
            }

            if (builder.menuItems.isNotEmpty()) {
                val menu = toolbar?.menu

                for ((index, menuItemId) in builder.menuItems.withIndex()) {
                    (menu?.findItem(menuItemId) as MenuItem).setOnMenuItemClickListener(builder.menuItemClickListeners[index])
                }
            }
        }
    }

    fun setMenuItemVisibility(menuItem: Int, visible: Boolean) {
        toolbar?.let { toolbar ->
            toolbar.menu.findItem(menuItem).isVisible = visible
        }
    }
}