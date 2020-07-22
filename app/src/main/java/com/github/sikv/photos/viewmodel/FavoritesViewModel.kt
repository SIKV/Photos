package com.github.sikv.photos.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.github.sikv.photos.App
import com.github.sikv.photos.data.repository.FavoritesRepository
import com.github.sikv.photos.database.FavoritePhotoEntity
import com.github.sikv.photos.enumeration.SortBy
import com.github.sikv.photos.event.Event
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.ui.dialog.OptionsBottomSheetDialog
import com.github.sikv.photos.util.getString
import kotlinx.coroutines.launch
import javax.inject.Inject

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var favoritesRepository: FavoritesRepository

    private val favoritesNewest: LiveData<List<FavoritePhotoEntity>>
    private val favoritesOldest: LiveData<List<FavoritePhotoEntity>>

    private val favoritesMediatorLiveData = MediatorLiveData<List<FavoritePhotoEntity>>()
    val favoritesLiveData: LiveData<List<FavoritePhotoEntity>> = favoritesMediatorLiveData

    private var sortBy = SortBy.DATE_ADDED_NEWEST

    private val removeAllResultMutableEvent = MutableLiveData<Event<Boolean>>()
    val removeAllResultEvent: LiveData<Event<Boolean>> = removeAllResultMutableEvent

    private var removeAllUndone = false

    init {
        App.instance.appComponent.inject(this)

        favoritesNewest = favoritesRepository.getFavoritesLiveData(SortBy.DATE_ADDED_NEWEST)
        favoritesOldest = favoritesRepository.getFavoritesLiveData(SortBy.DATE_ADDED_OLDEST)

        favoritesMediatorLiveData.addSource(favoritesNewest) { result ->
            if (sortBy == SortBy.DATE_ADDED_NEWEST) {
                favoritesMediatorLiveData.value = result
            }
        }

        favoritesMediatorLiveData.addSource(favoritesOldest) { result ->
            if (sortBy == SortBy.DATE_ADDED_OLDEST) {
                favoritesMediatorLiveData.value = result
            }
        }
    }

    fun invertFavorite(photo: Photo) {
        favoritesRepository.invertFavorite(photo)
    }

    /**
     * When the user presses "Remove all" action, in fact, it doesn't delete photos from the database.
     * Instead, the photos are marked as deleted and the user can press "Undo" to put them back.
     */
    fun markAllAsRemoved() {
        viewModelScope.launch {
            val markedAllAsRemoved = favoritesRepository.markAllAsRemoved()

            removeAllResultMutableEvent.value = Event(markedAllAsRemoved)
            removeAllUndone = false
        }
    }

    /**
     * Undoes "Remove all" action.
     */
    fun unmarkAllAsRemoved() {
        favoritesRepository.unmarkAllAsRemoved()

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

    fun createSortByDialog(): OptionsBottomSheetDialog {
        val options = SortBy.values().map { getString(it.text) }.toList()
        val selectedOptionIndex = SortBy.values().indexOf(sortBy)

        return OptionsBottomSheetDialog.newInstance(options, selectedOptionIndex) { index ->
            val selectedSortBy = SortBy.values()[index]

            when (selectedSortBy) {
                SortBy.DATE_ADDED_NEWEST -> favoritesNewest.value?.let { favoritesMediatorLiveData.value = it }
                SortBy.DATE_ADDED_OLDEST -> favoritesOldest.value?.let { favoritesMediatorLiveData.value = it }
            }

            sortBy = selectedSortBy
        }
    }
}