package com.github.sikv.photos.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.sikv.photos.domain.Photo
import com.github.sikv.photos.navigation.args.PhotoDetailsFragmentArguments
import com.github.sikv.photos.navigation.args.fragmentArguments
import com.github.sikv.photos.common.DownloadService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface PhotoUiState {

    object NoData: PhotoUiState

    data class Ready(
        val photo: Photo,
        val isFavorite: Boolean
    ) : PhotoUiState
}

@HiltViewModel
class PhotoDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val favoritesRepository: com.github.sikv.photos.data.repository.FavoritesRepository,
    private val downloadService: DownloadService
) : ViewModel() {

    private val mutableUiState = MutableStateFlow<PhotoUiState>(PhotoUiState.NoData)
    val uiState: StateFlow<PhotoUiState> = mutableUiState

    init {
        val photo = savedStateHandle.fragmentArguments<PhotoDetailsFragmentArguments>().photo

        mutableUiState.value = PhotoUiState.Ready(
            photo = photo,
            isFavorite = favoritesRepository.isFavorite(photo)
        )

        viewModelScope.launch {
            favoritesRepository.favoriteUpdates().collect { update ->
                when (update) {
                    is com.github.sikv.photos.data.repository.FavoritesRepository.UpdatePhoto -> {
                        when (val state = uiState.value) {
                            is PhotoUiState.Ready -> {
                                mutableUiState.value = state.copy(isFavorite = update.isFavorite)
                            }
                            else -> {}
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    fun toggleFavorite() {
        when (val state = uiState.value) {
            is PhotoUiState.Ready -> {
                favoritesRepository.invertFavorite(state.photo)
            }
            else -> {}
        }
    }

    fun downloadPhoto() {
        when (val state = uiState.value) {
            is PhotoUiState.Ready -> {
                downloadService.downloadPhoto(
                    photoUrl = state.photo.getPhotoDownloadUrl(),
                    notificationTitle = "Photos", // TODO Fix
                    notificationDescription = "Downloading photo" // TODO Fix
                )
            }
            else -> {}
        }
    }
}
