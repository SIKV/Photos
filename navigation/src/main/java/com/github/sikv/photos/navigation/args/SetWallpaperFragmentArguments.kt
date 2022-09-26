package com.github.sikv.photos.navigation.args

import com.github.sikv.photos.domain.Photo
import kotlinx.parcelize.Parcelize

@Parcelize
data class SetWallpaperFragmentArguments(
    val photo: Photo
) : FragmentArguments
