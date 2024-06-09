package com.github.sikv.photos.curated

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.github.sikv.photos.common.ui.findActivity
import com.github.sikv.photos.compose.ui.PhotoItem
import com.github.sikv.photos.compose.ui.PhotoItemCompact
import com.github.sikv.photos.compose.ui.Scaffold
import com.github.sikv.photos.domain.ListLayout
import com.github.sikv.photos.domain.Photo

// TODO: Add scrollable toolbar.
// TODO: Add loading indicator.

@Composable
internal fun CuratedPhotosScreen(
    onGoToPhotoDetails: (Photo) -> Unit,
    viewModel: CuratedPhotosViewModel,
) {
    val photos = viewModel.getCuratedPhotos().collectAsLazyPagingItems()
    val listLayout by viewModel.listLayoutState.collectAsStateWithLifecycle()

    Scaffold(
        title = stringResource(id = R.string.photos),
        actions = {
            SwitchLayoutAction(viewModel = viewModel)
        }
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(listLayout.spanCount)
        ) {
            items(photos.itemCount) { index ->
                photos[index]?.let { photo ->
                    Photo(
                        photo = photo,
                        listLayout = listLayout,
                        viewModel = viewModel,
                        onGoToPhotoDetails = onGoToPhotoDetails
                    )
                }
            }
        }
    }
}

@Composable
private fun Photo(
    photo: Photo,
    listLayout: ListLayout,
    viewModel: CuratedPhotosViewModel,
    onGoToPhotoDetails: (Photo) -> Unit
) {
    val context = LocalContext.current
    val isFavorite by viewModel.isFavorite(photo).collectAsStateWithLifecycle(initialValue = false)

    when (listLayout) {
        ListLayout.LIST -> {
            PhotoItem(
                photo = photo,
                isFavorite = isFavorite,
                onClick = {
                    onGoToPhotoDetails(photo)
                },
                onAttributionClick = {
                   viewModel.onPhotoAttributionClick(context.findActivity(), photo)
                },
                onMoreClick = {
                    viewModel.openActions(context.findActivity(), photo)
                },
                onToggleFavorite = {
                    viewModel.toggleFavorite(photo)
                },
                onShareClick = {
                    viewModel.sharePhoto(context.findActivity(), photo)
                },
                onDownloadClick = {
                    viewModel.downloadPhoto(context.findActivity(), photo)
                }
            )
        }
        ListLayout.GRID -> {
            PhotoItemCompact(
                photo = photo,
                onClick = {
                    onGoToPhotoDetails(photo)
                }
            )
        }
    }
}

@Composable
private fun SwitchLayoutAction(
    viewModel: CuratedPhotosViewModel
) {
    val listLayout by viewModel.listLayoutState.collectAsStateWithLifecycle()

    val icon = when (listLayout) {
        ListLayout.LIST -> R.drawable.ic_view_grid_24dp
        ListLayout.GRID -> R.drawable.ic_view_list_24dp
    }

    IconButton(
        onClick = viewModel::switchListLayout
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = stringResource(id = R.string.switch_layout)
        )
    }
}
