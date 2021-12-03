package com.github.sikv.photos.ui.compose.state

import com.github.sikv.photos.model.Photo

sealed class PhotoViewState {

    object NoData: PhotoViewState()

    data class Ready(
        val photo: Photo,
        val isFavorite: Boolean
    ) : PhotoViewState()
}
