package com.github.sikv.photos.route.impl

import com.github.sikv.photos.feedback.FeedbackFragment
import com.github.sikv.photos.navigation.Navigation
import com.github.sikv.photos.navigation.route.FeedbackRoute
import javax.inject.Inject

class FeedbackRouteImpl @Inject constructor() : FeedbackRoute {

    override fun present(navigation: Navigation?) {
        navigation?.addFragment(FeedbackFragment())
    }
}
