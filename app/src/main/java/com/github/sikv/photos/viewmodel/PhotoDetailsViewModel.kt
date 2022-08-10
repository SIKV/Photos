package com.github.sikv.photos.viewmodel

import androidx.lifecycle.*
import com.github.sikv.photos.data.repository.FavoritesRepository
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.service.DownloadService
import com.github.sikv.photos.ui.fragment.PhotoDetailsFragmentArguments
import com.github.sikv.photos.ui.fragmentArguments
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
    private val favoritesRepository: FavoritesRepository,
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
                    is FavoritesRepository.UpdatePhoto -> {
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
                downloadService.downloadPhoto(state.photo.getPhotoDownloadUrl())
            }
            else -> {}
        }
    }
}
