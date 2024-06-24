package com.github.sikv.photos.favorites

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.github.sikv.photos.common.PreferencesService
import com.github.sikv.photos.common.ui.OptionsBottomSheetDialog
import com.github.sikv.photos.data.SortBy
import com.github.sikv.photos.data.repository.FavoritesRepository2
import com.github.sikv.photos.domain.ListLayout
import com.github.sikv.photos.domain.Photo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class FavoritesViewModel @Inject constructor(
    application: Application,
    private val favoritesRepository: FavoritesRepository2,
    private val preferencesService: PreferencesService
) : AndroidViewModel(application) {

    private var sortBy = SortBy.DATE_ADDED_NEWEST

    private val mutableUiState = MutableStateFlow(
        FavoritesUiState(
            photos = emptyList(),
            listLayout = preferencesService.getFavoritesListLayout(),
            shouldShowRemovedNotification = false
        )
    )
    val uiState: StateFlow<FavoritesUiState> = mutableUiState

    init {
        emitFavorites()
    }

    fun isFavorite(photo: Photo): Flow<Boolean> {
        return favoritesRepository.getFavorites()
            .map { photos ->
                photos.contains(photo)
            }
    }

    fun toggleFavorite(photo: Photo) {
        viewModelScope.launch {
            favoritesRepository.invertFavorite(photo)
        }
    }

    private fun emitFavorites() {
        viewModelScope.launch {
            favoritesRepository.getFavorites(sortBy).collect { photos ->
                mutableUiState.update { state ->
                    state.copy(photos = photos)
                }
            }
        }
    }

    /**
     * When the user presses "Remove all" action, in fact, it doesn't delete photos from the database.
     * Instead, the photos are marked as deleted and the user can press "Undo" to put them back.
     */
    fun markAllAsRemoved() {
        viewModelScope.launch {
            val markedAllAsRemoved = favoritesRepository.markAllAsDeleted()

            mutableUiState.update { state ->
                state.copy(shouldShowRemovedNotification = markedAllAsRemoved)
            }
        }
    }

    /**
     * Undoes "Remove all" action.
     */
    fun unmarkAllAsRemoved() {
        viewModelScope.launch {
            favoritesRepository.unmarkAllAsDeleted()

            mutableUiState.update { state ->
                state.copy(shouldShowRemovedNotification = false)
            }
        }
    }

    /**
     * Deletes photos from the Favorites database if "Remove all" action hasn't been undone.
     */
    fun removeAllMarked() {
        viewModelScope.launch {
            favoritesRepository.deleteAllMarked()

            mutableUiState.update { state ->
                state.copy(shouldShowRemovedNotification = false)
            }
        }
    }

    fun switchListLayout() {
        mutableUiState.update { state ->
            val switchedListLayout =  when (state.listLayout) {
                ListLayout.LIST -> ListLayout.GRID
                ListLayout.GRID -> ListLayout.LIST
            }
            preferencesService.setFavoritesListLayout(switchedListLayout)
            state.copy(listLayout = switchedListLayout)
        }
    }

    fun createSortByDialog(): OptionsBottomSheetDialog {
        val options = SortBy.entries.map { getString(it.getTitle()) }.toList()
        val selectedOptionIndex = SortBy.entries.indexOf(sortBy)

        return OptionsBottomSheetDialog.newInstance(options, selectedOptionIndex) { index ->
            val selectedSortBy = SortBy.entries[index]
            sortBy = selectedSortBy

            emitFavorites()
        }
    }
}

@StringRes
private fun SortBy.getTitle(): Int {
    return when (this) {
        SortBy.DATE_ADDED_NEWEST -> R.string.date_added_newest
        SortBy.DATE_ADDED_OLDEST -> R.string.date_added_oldest
    }
}

private fun AndroidViewModel.getString(@StringRes id: Int): String {
    return getApplication<Application>().resources.getString(id)
}
