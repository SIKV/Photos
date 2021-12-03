package com.github.sikv.photos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.github.sikv.photos.data.repository.FavoritesRepository
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.service.DownloadService
import com.github.sikv.photos.ui.compose.state.PhotoViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PhotoDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val favoritesRepository: FavoritesRepository,
    private val downloadService: DownloadService
) : ViewModel(), FavoritesRepository.Listener {

    private val mutableViewState = MutableLiveData<PhotoViewState>(PhotoViewState.NoData)
    val viewState: LiveData<PhotoViewState> = mutableViewState

    init {
        val photo = requireNotNull(savedStateHandle.get<Photo>(Photo.KEY))

        favoritesRepository.subscribe(this)

        mutableViewState.value = PhotoViewState.Ready(
            photo = photo,
            isFavorite = favoritesRepository.isFavorite(photo)
        )
    }

    fun toggleFavorite() {
        when (val state = viewState.value) {
            is PhotoViewState.Ready -> {
                favoritesRepository.invertFavorite(state.photo)
            }
        }
    }

    fun downloadPhoto() {
        when (val state = viewState.value) {
            is PhotoViewState.Ready -> {
                downloadService.downloadPhoto(state.photo.getPhotoDownloadUrl())
            }
        }
    }

    override fun onCleared() {
        super.onCleared()

        favoritesRepository.unsubscribe(this)
    }

    override fun onFavoriteChanged(photo: Photo, isFavorite: Boolean) {
        when (val state = viewState.value) {
            is PhotoViewState.Ready -> {
                mutableViewState.postValue(state.copy(isFavorite = isFavorite))
            }
        }
    }

    override fun onFavoritesChanged() {}
}
