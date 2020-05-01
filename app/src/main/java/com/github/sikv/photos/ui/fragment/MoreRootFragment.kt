package com.github.sikv.photos.ui.fragment

import androidx.fragment.app.Fragment

class MoreRootFragment : RootFragment() {

    override fun provideRootFragment(): Fragment {
        return MoreFragment()
    }
}