package com.github.sikv.photos.navigation.args

import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchFragmentArguments(
    val query: String? = null
) : FragmentArguments
