package com.github.sikv.photos.route.impl

import androidx.navigation.NavController
import com.github.sikv.photos.R
import com.github.sikv.photos.navigation.route.FeedbackRoute
import javax.inject.Inject

class FeedbackRouteImpl @Inject constructor() : FeedbackRoute {

    override fun present(navController: NavController) {
        navController.navigate(R.id.navigateToFeedback)
    }
}
