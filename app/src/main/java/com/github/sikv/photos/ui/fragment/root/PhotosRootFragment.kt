package com.github.sikv.photos.ui.fragment.root

import androidx.fragment.app.Fragment
import com.github.sikv.photos.ui.fragment.PhotosFragment

class PhotosRootFragment : RootFragment() {

    override fun provideRootFragment(): Fragment {
        return PhotosFragment()
    }
}