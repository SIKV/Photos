package com.github.sikv.photos.navigation.args

import com.github.sikv.photos.domain.Photo
import kotlinx.parcelize.Parcelize

@Parcelize
data class PhotoDetailsFragmentArguments(
    val photo: Photo
) : FragmentArguments
