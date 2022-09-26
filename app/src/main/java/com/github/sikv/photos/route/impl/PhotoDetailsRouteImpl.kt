package com.github.sikv.photos.route.impl

import com.github.sikv.photos.navigation.Navigation
import com.github.sikv.photos.navigation.NavigationAnimation
import com.github.sikv.photos.navigation.args.PhotoDetailsFragmentArguments
import com.github.sikv.photos.navigation.args.withArguments
import com.github.sikv.photos.navigation.route.PhotoDetailsRoute
import com.github.sikv.photos.photo.details.PhotoDetailsFragment
import javax.inject.Inject

class PhotoDetailsRouteImpl @Inject constructor() : PhotoDetailsRoute {

    override fun present(navigation: Navigation?, args: PhotoDetailsFragmentArguments) {
        val photoDetailsFragment = PhotoDetailsFragment()
            .withArguments(args)

        navigation?.addFragment(photoDetailsFragment,
            animation = NavigationAnimation.SLIDE_V
        )
    }
}
