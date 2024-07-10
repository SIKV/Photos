package com.github.sikv.photos.photo.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.sikv.photos.data.repository.FavoritesRepository2
import com.github.sikv.photos.navigation.args.PhotoDetailsFragmentArguments
import com.github.sikv.photos.navigation.args.fragmentArguments
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class PhotoDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val favoritesRepository: FavoritesRepository2
) : ViewModel() {

    private val mutableUiState: MutableStateFlow<PhotoUiState>
    val uiState: StateFlow<PhotoUiState>

    init {
        val photo = savedStateHandle.fragmentArguments<PhotoDetailsFragmentArguments>().photo

        mutableUiState = MutableStateFlow(PhotoUiState(
            photo = photo,
            isFavorite = false,
        ))

        uiState = mutableUiState

        viewModelScope.launch {
            val isFavorite = favoritesRepository.isFavorite(photo)
            mutableUiState.update { state ->
                state.copy(isFavorite = isFavorite)
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            favoritesRepository.invertFavorite(uiState.value.photo)

            mutableUiState.update { state ->
                state.copy(isFavorite = !state.isFavorite)
            }
        }
    }
}
