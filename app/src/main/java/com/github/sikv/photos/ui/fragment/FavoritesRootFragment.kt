package com.github.sikv.photos.ui.fragment

import androidx.fragment.app.Fragment

class FavoritesRootFragment : RootFragment() {

    override fun provideRootFragment(): Fragment {
        return FavoritesFragment()
    }
}