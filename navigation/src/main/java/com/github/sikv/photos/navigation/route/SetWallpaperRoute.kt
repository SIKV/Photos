package com.github.sikv.photos.navigation.route

import androidx.fragment.app.FragmentManager
import com.github.sikv.photos.navigation.args.SetWallpaperFragmentArguments

interface SetWallpaperRoute {
    fun present(fragmentManager: FragmentManager, args: SetWallpaperFragmentArguments)
}
