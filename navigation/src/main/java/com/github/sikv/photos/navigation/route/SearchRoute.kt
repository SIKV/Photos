package com.github.sikv.photos.navigation.route

import androidx.navigation.NavController
import com.github.sikv.photos.navigation.args.SearchFragmentArguments

interface SearchRoute {
    fun present(navController: NavController, args: SearchFragmentArguments)
}
