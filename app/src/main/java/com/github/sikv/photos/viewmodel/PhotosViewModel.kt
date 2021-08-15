package com.github.sikv.photos.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.liveData
import com.github.sikv.photos.config.ListConfig
import com.github.sikv.photos.data.CuratedPhotosPagingSource
import com.github.sikv.photos.data.repository.FavoritesRepository
import com.github.sikv.photos.data.repository.PhotosRepository
import com.github.sikv.photos.enumeration.ListLayout
import com.github.sikv.photos.enumeration.SavedPreference
import com.github.sikv.photos.event.Event
import com.github.sikv.photos.event.VoidEvent
import com.github.sikv.photos.model.Photo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PhotosViewModel @Inject constructor(
        private val photosRepository: PhotosRepository,
        private val favoritesRepository: FavoritesRepository,
        private val preferences: SharedPreferences
) : ViewModel(), FavoritesRepository.Listener {

    private val favoriteChangedMutableEvent = MutableLiveData<Event<Photo>>()
    val favoriteChangedEvent: LiveData<Event<Photo>> = favoriteChangedMutableEvent

    private val favoritesChangedMutableEvent = MutableLiveData<VoidEvent>()
    val favoritesChangedEvent: LiveData<VoidEvent> = favoritesChangedMutableEvent

    private val listLayoutChangedMutable = MutableLiveData<ListLayout>()
    val listLayoutChanged: LiveData<ListLayout> = listLayoutChangedMutable

    init {
        favoritesRepository.subscribe(this)

        val listLayout = ListLayout.findBySpanCount(
                preferences.getInt(SavedPreference.CURATED_LIST_LAYOUT.key, ListLayout.LIST.spanCount)
        )
        listLayoutChangedMutable.value = listLayout
    }

    override fun onCleared() {
        super.onCleared()

        favoritesRepository.unsubscribe(this)
    }

    override fun onFavoriteChanged(photo: Photo, favorite: Boolean) {
        favoriteChangedMutableEvent.postValue(Event(photo))
    }

    override fun onFavoritesChanged() {
        favoritesChangedMutableEvent.postValue(VoidEvent())
    }

    fun invertFavorite(photo: Photo) {
        favoritesRepository.invertFavorite(photo)
    }

    fun updateListLayout(listLayout: ListLayout) {
        preferences.edit()
                .putInt(SavedPreference.CURATED_LIST_LAYOUT.key, listLayout.spanCount)
                .apply()

        listLayoutChangedMutable.value = listLayout
    }

    fun getCuratedPhotos(): LiveData<PagingData<Photo>> {
        return Pager(
                config = ListConfig.pagingConfig,
                pagingSourceFactory = {
                    CuratedPhotosPagingSource(photosRepository)
                }
        ).liveData
    }
}