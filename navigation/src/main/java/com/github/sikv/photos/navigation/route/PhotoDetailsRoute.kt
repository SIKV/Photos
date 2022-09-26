package com.github.sikv.photos.navigation.route

import com.github.sikv.photos.navigation.Navigation
import com.github.sikv.photos.navigation.args.PhotoDetailsFragmentArguments

interface PhotoDetailsRoute {
    fun present(navigation: Navigation?, args: PhotoDetailsFragmentArguments)
}
