package com.github.sikv.photos.ui.state

import com.github.sikv.photos.model.Photo

data class PhotoState(
    val photo: Photo,
    val isFavorite: Boolean
)
