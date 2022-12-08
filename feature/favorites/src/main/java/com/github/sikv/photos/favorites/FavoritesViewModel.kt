package com.github.sikv.photos.favorites

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.github.sikv.photos.common.PreferencesService
import com.github.sikv.photos.common.ui.OptionsBottomSheetDialog
import com.github.sikv.photos.data.repository.FavoritesRepository
import com.github.sikv.photos.domain.ListLayout
import com.github.sikv.photos.domain.Photo
import com.github.sikv.photos.data.SortBy
import com.github.sikv.photos.data.persistence.FavoritePhotoEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface FavoritesUiState {
    data class Data(
        val photos: List<FavoritePhotoEntity>,
        val listLayout: ListLayout,
        val shouldShowRemovedNotification: Boolean
    ) : FavoritesUiState
}

@HiltViewModel
internal class FavoritesViewModel @Inject constructor(
    application: Application,
    private val favoritesRepository: FavoritesRepository,
    private val preferencesService: PreferencesService
) : AndroidViewModel(application) {

    private var sortBy = SortBy.DATE_ADDED_NEWEST

    private var removeAllUndone = false

    private val mutableUiState = MutableStateFlow<FavoritesUiState>(
        FavoritesUiState.Data(
            photos = emptyList(),
            listLayout = preferencesService.getFavoritesListLayout(),
            shouldShowRemovedNotification = false
        )
    )
    val uiState: StateFlow<FavoritesUiState> = mutableUiState

    init {
        emitFavorites()
    }

    private fun updateUiState(update: (currentUiState: FavoritesUiState.Data) -> FavoritesUiState.Data) {
        val stateValue = mutableUiState.value

        if (stateValue is FavoritesUiState.Data) {
            mutableUiState.value = update(stateValue)
        }
    }

    private fun emitFavorites() {
        viewModelScope.launch {
            favoritesRepository.getFavorites(sortBy).collect { photos ->
                updateUiState { currentUiState ->
                    currentUiState.copy(photos = photos)
                }
            }
        }
    }

    fun toggleFavorite(photo: Photo) {
        favoritesRepository.invertFavorite(photo)
    }

    /**
     * When the user presses "Remove all" action, in fact, it doesn't delete photos from the database.
     * Instead, the photos are marked as deleted and the user can press "Undo" to put them back.
     */
    fun markAllAsRemoved() {
        viewModelScope.launch {
            val markedAllAsRemoved = favoritesRepository.markAllAsRemoved()

            updateUiState { currentUiState ->
                currentUiState.copy(shouldShowRemovedNotification = markedAllAsRemoved)
            }
            removeAllUndone = false
        }
    }

    /**
     * Undoes "Remove all" action.
     */
    fun unmarkAllAsRemoved() {
        favoritesRepository.unmarkAllAsRemoved()

        updateUiState { currentUiState ->
            currentUiState.copy(shouldShowRemovedNotification = false)
        }
        removeAllUndone = true
    }

    /**
     * Deletes photos from the Favorites database if "Remove all" action hasn't been undone.
     */
    fun removeAllIfNotUndone() {
        if (!removeAllUndone) {
            favoritesRepository.removeAll()
        }
    }

    fun updateListLayout(listLayout: ListLayout) {
        preferencesService.setFavoritesListLayout(listLayout)

        updateUiState { currentUiState ->
            currentUiState.copy(listLayout = listLayout)
        }
    }

    fun createSortByDialog(): OptionsBottomSheetDialog {
        val options = SortBy.values().map { getString(it.getTitle()) }.toList()
        val selectedOptionIndex = SortBy.values().indexOf(sortBy)

        return OptionsBottomSheetDialog.newInstance(options, selectedOptionIndex) { index ->
            val selectedSortBy = SortBy.values()[index]
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
