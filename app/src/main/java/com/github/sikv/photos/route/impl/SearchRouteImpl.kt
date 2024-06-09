package com.github.sikv.photos.route.impl

import androidx.navigation.NavController
import com.github.sikv.photos.R
import com.github.sikv.photos.navigation.args.SearchFragmentArguments
import com.github.sikv.photos.navigation.navigate
import com.github.sikv.photos.navigation.route.SearchRoute
import javax.inject.Inject

class SearchRouteImpl @Inject constructor() : SearchRoute {

    override fun present(navController: NavController, args: SearchFragmentArguments) {
        navController.navigate(R.id.navigateToSearch, args)
    }
}
