package com.github.sikv.photos.compose.ui

import androidx.compose.runtime.Composable
import com.github.sikv.photos.domain.ListLayout
import com.github.sikv.photos.domain.Photo

@Composable
fun DynamicPhotoItem(
    photo: Photo,
    isFavorite: Boolean,
    listLayout: ListLayout,
    onPhotoClick: (Photo) -> Unit,
    onPhotoAttributionClick: (Photo) -> Unit,
    onPhotoActionsClick: (Photo) -> Unit,
    onToggleFavoriteClick: (Photo) -> Unit,
    onSharePhotoClick: (Photo) -> Unit,
    onDownloadPhotoClick: (Photo) -> Unit
) {
    when (listLayout) {
        ListLayout.LIST -> {
            PhotoItem(
                photo = photo,
                isFavorite = isFavorite,
                onClick = {
                    onPhotoClick(photo)
                },
                onAttributionClick = {
                    onPhotoAttributionClick(photo)
                },
                onMoreClick = {
                    onPhotoActionsClick(photo)
                },
                onToggleFavorite = {
                    onToggleFavoriteClick(photo)
                },
                onShareClick = {
                    onSharePhotoClick(photo)
                },
                onDownloadClick = {
                    onDownloadPhotoClick(photo)
                }
            )
        }
        ListLayout.GRID -> {
            PhotoItemCompact(
                photo = photo,
                onClick = {
                    onPhotoClick(photo)
                }
            )
        }
    }
}
