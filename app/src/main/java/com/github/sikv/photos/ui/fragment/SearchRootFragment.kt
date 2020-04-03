package com.github.sikv.photos.ui.fragment

import androidx.fragment.app.Fragment

class SearchRootFragment : RootFragment() {

    override fun provideRootFragment(): Fragment {
        return SearchDashboardFragment()
    }
}