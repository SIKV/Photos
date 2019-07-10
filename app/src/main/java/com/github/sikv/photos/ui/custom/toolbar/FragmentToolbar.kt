package com.github.sikv.photos.ui.custom.toolbar

import androidx.annotation.IdRes
import androidx.annotation.MenuRes
import android.view.MenuItem


class FragmentToolbar(
        @IdRes val resId: Int,
        @MenuRes val menuId: Int,
        val menuItems: List<Int>,
        val menuItemClickListeners: List<MenuItem.OnMenuItemClickListener?>) {

    companion object {
        const val NO_TOOLBAR = -1
    }

    class Builder {
        private var resId: Int = -1
        private var menuId: Int = -1
        private var menuItems: List<Int> = listOf()
        private var menuItemClickListeners: List<MenuItem.OnMenuItemClickListener?> = listOf()

        fun withId(@IdRes resId: Int) = apply { this.resId = resId }

        fun withMenu(@MenuRes menuId: Int) = apply { this.menuId = menuId }

        fun withMenuItems(menuItems: List<Int>, menuItemClickListeners: List<MenuItem.OnMenuItemClickListener?>) = apply {
            this.menuItems = menuItems
            this.menuItemClickListeners = menuItemClickListeners
        }

        fun build() = FragmentToolbar(resId, menuId, menuItems, menuItemClickListeners)
    }
}