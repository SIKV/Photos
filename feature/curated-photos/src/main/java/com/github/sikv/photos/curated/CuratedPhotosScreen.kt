package com.github.sikv.photos.curated

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.github.sikv.photos.compose.ui.DynamicPhotoItem
import com.github.sikv.photos.compose.ui.Scaffold
import com.github.sikv.photos.compose.ui.ShimmerPhotoItem
import com.github.sikv.photos.compose.ui.SwitchLayoutAction
import com.github.sikv.photos.domain.Photo

private const val loadingItemCount = 12

@Composable
internal fun CuratedPhotosScreen(
    onPhotoClick: (Photo) -> Unit,
    onPhotoAttributionClick: (Photo) -> Unit,
    onPhotoActionsClick: (Photo) -> Unit,
    onSharePhotoClick: (Photo) -> Unit,
    onDownloadPhotoClick: (Photo) -> Unit,
    viewModel: CuratedPhotosViewModel = hiltViewModel(),
) {
    val photos = viewModel.curatedPhotos.collectAsLazyPagingItems()
    val loadState = photos.loadState.refresh

    val listLayout by viewModel.listLayoutState.collectAsStateWithLifecycle()

    Scaffold(
        title = { Text(stringResource(id = R.string.photos)) },
        actions = {
            SwitchLayoutAction(
                listLayout = listLayout,
                onSwitchLayoutClick = viewModel::switchListLayout
            )
        }
    ) {
        when (loadState) {
            is LoadState.Loading -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(listLayout.spanCount)
                ) {
                    items(loadingItemCount) {
                        ShimmerPhotoItem(listLayout)
                    }
                }
            }
            is LoadState.Error -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Text(
                        text = stringResource(id = R.string.error),
                        textAlign = TextAlign.Center,
                    )
                }
            }
            is LoadState.NotLoading -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(listLayout.spanCount)
                ) {
                    items(photos.itemCount) { index ->
                        photos[index]?.let { photo ->
                            val isFavorite by viewModel.isFavorite(photo)
                                .collectAsStateWithLifecycle(initialValue = false)

                            DynamicPhotoItem(
                                photo = photo,
                                isFavorite = isFavorite,
                                listLayout = listLayout,
                                onPhotoClick = onPhotoClick,
                                onPhotoAttributionClick = onPhotoAttributionClick,
                                onPhotoActionsClick = onPhotoActionsClick,
                                onToggleFavoriteClick = viewModel::toggleFavorite,
                                onSharePhotoClick = onSharePhotoClick,
                                onDownloadPhotoClick = onDownloadPhotoClick
                            )
                        }
                    }
                }
            }
        }
    }
}
