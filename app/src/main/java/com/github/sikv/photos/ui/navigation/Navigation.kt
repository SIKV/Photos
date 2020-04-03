package com.github.sikv.photos.ui.navigation

import androidx.fragment.app.Fragment

interface Navigation {
    fun addFragment(fragment: Fragment)
    fun backPressed(): Boolean
    fun backToRoot(): Fragment?
}