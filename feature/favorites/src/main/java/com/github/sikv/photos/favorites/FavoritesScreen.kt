package com.github.sikv.photos.favorites

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.sikv.photos.compose.ui.DynamicPhotoItem
import com.github.sikv.photos.compose.ui.NoContent
import com.github.sikv.photos.compose.ui.Scaffold
import com.github.sikv.photos.compose.ui.SwitchLayoutAction
import com.github.sikv.photos.domain.Photo
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

@Composable
internal fun FavoritesScreen(
    onPhotoClick: (Photo) -> Unit,
    onPhotoAttributionClick: (Photo) -> Unit,
    onPhotoActionsClick: (Photo) -> Unit,
    onSharePhotoClick: (Photo) -> Unit,
    onDownloadPhotoClick: (Photo) -> Unit,
    onShowDialog: (BottomSheetDialogFragment) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val removedMessage = stringResource(id = R.string.removed)
    val undoActionLabel = stringResource(id = R.string.undo)

    LaunchedEffect(uiState.shouldShowRemovedNotification) {
        if (uiState.shouldShowRemovedNotification) {
            scope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = removedMessage,
                    actionLabel = undoActionLabel,
                    duration = SnackbarDuration.Long
                )
                when (result) {
                    SnackbarResult.Dismissed -> viewModel.removeAllMarked()
                    SnackbarResult.ActionPerformed -> viewModel.unmarkAllAsRemoved()
                }
            }
        }
    }

    Scaffold(
        title = { Text(stringResource(id = R.string.favorites)) },
        actions = {
            SwitchLayoutAction(
                listLayout = uiState.listLayout,
                onSwitchLayoutClick = viewModel::switchListLayout
            )
            MenuAction(
                onSortClick = {
                    onShowDialog(viewModel.createSortByDialog())
                },
                onRemoveAllClick = {
                    viewModel.markAllAsRemoved()
                }
            )
        },
        snackbarHost = { 
            SnackbarHost(hostState = snackbarHostState)
        }
    ) {
        if (uiState.photos.isEmpty()) {
            NoContent(
                title = stringResource(id = R.string.no_favorites)
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(uiState.listLayout.spanCount)
            ) {
                items(uiState.photos.size) { index ->
                    val photo = uiState.photos[index]

                    val isFavorite by viewModel.isFavorite(photo)
                        .collectAsStateWithLifecycle(initialValue = false)

                    DynamicPhotoItem(
                        photo = photo,
                        isFavorite = isFavorite,
                        listLayout = uiState.listLayout,
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

@Composable
private fun MenuAction(
    onSortClick: () -> Unit,
    onRemoveAllClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(
        onClick = {
            expanded = true
        }
    ) {
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = stringResource(id = R.string.more)
        )
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = {
            expanded = false
        }
    ) {
        DropdownMenuItem(
            text = {  Text(stringResource(R.string.sort_by)) },
            onClick = {
                expanded = false
                onSortClick()
            }
        )
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.remove_all)) },
            onClick = {
                expanded = false
                onRemoveAllClick()
            }
        )
    }
}
