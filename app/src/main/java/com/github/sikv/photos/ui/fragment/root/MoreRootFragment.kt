package com.github.sikv.photos.ui.fragment.root

import androidx.fragment.app.Fragment
import com.github.sikv.photos.ui.fragment.MoreFragment

class MoreRootFragment : RootFragment() {

    override fun provideRootFragment(): Fragment {
        return MoreFragment()
    }
}