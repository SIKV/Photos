package com.github.sikv.photos.route.impl

import androidx.fragment.app.FragmentManager
import com.github.sikv.photos.navigation.args.SetWallpaperFragmentArguments
import com.github.sikv.photos.navigation.args.withArguments
import com.github.sikv.photos.navigation.route.SetWallpaperRoute
import com.github.sikv.photos.wallpaper.SetWallpaperDialog
import javax.inject.Inject

class SetWallpaperRouteImpl @Inject constructor() : SetWallpaperRoute {

    override fun present(fragmentManager: FragmentManager, args: SetWallpaperFragmentArguments) {
        SetWallpaperDialog()
            .withArguments(args)
            .show(fragmentManager, "SetWallpaperDialog")
    }
}
