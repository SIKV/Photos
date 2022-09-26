package com.github.sikv.photos.ui.fragment.root

import androidx.fragment.app.Fragment
import com.github.sikv.photos.favorites.FavoritesFragment

class FavoritesRootFragment : RootFragment() {

    override fun provideRootFragment(): Fragment {
        return FavoritesFragment()
    }
}
