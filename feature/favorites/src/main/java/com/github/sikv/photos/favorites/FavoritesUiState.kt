package com.github.sikv.photos.favorites

import com.github.sikv.photos.data.persistence.FavoritePhotoEntity
import com.github.sikv.photos.domain.ListLayout

internal data class FavoritesUiState(
    val photos: List<FavoritePhotoEntity>,
    val listLayout: ListLayout,
    val shouldShowRemovedNotification: Boolean
)
