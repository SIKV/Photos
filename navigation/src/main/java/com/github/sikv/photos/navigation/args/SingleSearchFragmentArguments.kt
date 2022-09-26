package com.github.sikv.photos.navigation.args

import com.github.sikv.photos.domain.PhotoSource
import kotlinx.parcelize.Parcelize

@Parcelize
data class SingleSearchFragmentArguments(
    val photoSource: PhotoSource
) : FragmentArguments
