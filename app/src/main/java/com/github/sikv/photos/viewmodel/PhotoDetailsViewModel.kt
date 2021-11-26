package com.github.sikv.photos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.github.sikv.photos.data.repository.FavoritesRepository
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.ui.state.PhotoState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PhotoDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val favoritesRepository: FavoritesRepository
) : ViewModel(), FavoritesRepository.Listener {

    private val photo = requireNotNull(savedStateHandle.get<Photo>(Photo.KEY))

    private val mutableUiState = MutableLiveData<PhotoState>()
    val uiState: LiveData<PhotoState> = mutableUiState

    init {
        mutableUiState.value = PhotoState(
            photo = photo,
            isFavorite = false
        )

        favoritesRepository.subscribe(this)

        val isFavorite = favoritesRepository.isFavorite(photo)

        mutableUiState.postValue(
            uiState.value?.copy(isFavorite = isFavorite)
        )
    }

    fun toggleFavorite() {
        val photo = uiState.value?.photo

        if (photo != null) {
            favoritesRepository.invertFavorite(photo)
        }
    }

    override fun onCleared() {
        super.onCleared()

        favoritesRepository.unsubscribe(this)
    }

    override fun onFavoriteChanged(photo: Photo, favorite: Boolean) {
        mutableUiState.postValue(
            uiState.value?.copy(isFavorite = favorite)
        )
    }

    override fun onFavoritesChanged() { }
}
