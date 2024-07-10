package com.github.sikv.photos.photo.details

import com.github.sikv.photos.domain.Photo

internal data class PhotoUiState(
    val photo: Photo,
    val isFavorite: Boolean
)
