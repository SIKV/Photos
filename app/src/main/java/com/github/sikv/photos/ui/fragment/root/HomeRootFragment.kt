package com.github.sikv.photos.ui.fragment.root

import androidx.fragment.app.Fragment
import com.github.sikv.photos.curated.CuratedPhotosFragment

class HomeRootFragment : RootFragment() {

    override fun provideRootFragment(): Fragment {
        return CuratedPhotosFragment()
    }
}
