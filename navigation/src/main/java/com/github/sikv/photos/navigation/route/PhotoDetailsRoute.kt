package com.github.sikv.photos.navigation.route

import androidx.navigation.NavController
import com.github.sikv.photos.navigation.args.PhotoDetailsFragmentArguments

interface PhotoDetailsRoute {
    fun present(navController: NavController, args: PhotoDetailsFragmentArguments)
}
