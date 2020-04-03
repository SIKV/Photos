package com.github.sikv.photos.ui.fragment

import androidx.fragment.app.Fragment

class SettingsRootFragment : RootFragment() {

    override fun provideRootFragment(): Fragment {
        return SettingsFragment()
    }
}