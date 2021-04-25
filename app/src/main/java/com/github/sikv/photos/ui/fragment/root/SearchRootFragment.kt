package com.github.sikv.photos.ui.fragment.root

import androidx.fragment.app.Fragment
import com.github.sikv.photos.ui.fragment.SearchDashboardFragment

class SearchRootFragment : RootFragment() {

    override fun provideRootFragment(): Fragment {
        return SearchDashboardFragment()
    }
}