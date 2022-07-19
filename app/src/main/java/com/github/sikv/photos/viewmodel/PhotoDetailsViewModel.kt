package com.github.sikv.photos.viewmodel

import androidx.lifecycle.*
import com.github.sikv.photos.data.repository.FavoritesRepository
import com.github.sikv.photos.service.DownloadService
import com.github.sikv.photos.ui.compose.state.PhotoViewState
import com.github.sikv.photos.ui.fragment.PhotoDetailsFragmentArguments
import com.github.sikv.photos.ui.fragmentArguments
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val favoritesRepository: FavoritesRepository,
    private val downloadService: DownloadService
) : ViewModel() {

    private val mutableViewState = MutableLiveData<PhotoViewState>(PhotoViewState.NoData)
    val viewState: LiveData<PhotoViewState> = mutableViewState

    init {
        val photo = savedStateHandle.fragmentArguments<PhotoDetailsFragmentArguments>().photo

        mutableViewState.value = PhotoViewState.Ready(
            photo = photo,
            isFavorite = favoritesRepository.isFavorite(photo)
        )

        viewModelScope.launch {
            favoritesRepository.favoriteUpdates().collect { update ->
                when (update) {
                    is FavoritesRepository.UpdatePhoto -> {
                        when (val state = viewState.value) {
                            is PhotoViewState.Ready -> {
                                mutableViewState.postValue(state.copy(isFavorite = update.isFavorite))
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
        when (val state = viewState.value) {
            is PhotoViewState.Ready -> {
                favoritesRepository.invertFavorite(state.photo)
            }
            else -> {}
        }
    }

    fun downloadPhoto() {
        when (val state = viewState.value) {
            is PhotoViewState.Ready -> {
                downloadService.downloadPhoto(state.photo.getPhotoDownloadUrl())
            }
            else -> {}
        }
    }
}
