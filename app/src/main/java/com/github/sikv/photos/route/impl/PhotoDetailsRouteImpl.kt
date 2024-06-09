package com.github.sikv.photos.route.impl

import androidx.navigation.NavController
import com.github.sikv.photos.R
import com.github.sikv.photos.navigation.args.PhotoDetailsFragmentArguments
import com.github.sikv.photos.navigation.navigate
import com.github.sikv.photos.navigation.route.PhotoDetailsRoute
import javax.inject.Inject

class PhotoDetailsRouteImpl @Inject constructor() : PhotoDetailsRoute {

    override fun present(navController: NavController, args: PhotoDetailsFragmentArguments) {
        navController.navigate(R.id.navigateToPhotoDetails, args)
    }
}
