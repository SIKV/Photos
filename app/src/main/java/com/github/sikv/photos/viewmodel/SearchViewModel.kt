package com.github.sikv.photos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.github.sikv.photos.config.ListConfig
import com.github.sikv.photos.data.SearchPhotosPagingSource
import com.github.sikv.photos.data.repository.FavoritesRepository
import com.github.sikv.photos.data.repository.PhotosRepository
import com.github.sikv.photos.enumeration.PhotoSource
import com.github.sikv.photos.event.Event
import com.github.sikv.photos.event.VoidEvent
import com.github.sikv.photos.model.Photo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
        private val photosRepository: PhotosRepository,
        private val favoritesRepository: FavoritesRepository
) : ViewModel(), FavoritesRepository.Listener {

    private val pagingConfig = PagingConfig(
            initialLoadSize = ListConfig.INITIAL_LOAD_SIZE,
            pageSize = ListConfig.PAGE_SIZE,
            enablePlaceholders = false
    )

    private val favoriteChangedMutableEvent = MutableLiveData<Event<Photo>>()
    val favoriteChangedEvent: LiveData<Event<Photo>> = favoriteChangedMutableEvent

    private val favoritesChangedMutableEvent = MutableLiveData<VoidEvent>()
    val favoritesChangedEvent: LiveData<VoidEvent> = favoritesChangedMutableEvent

    init {
        favoritesRepository.subscribe(this)
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

    fun searchPhotos(photoSource: PhotoSource, query: String): LiveData<PagingData<Photo>>? {
        val queryTrimmed = query.trim()

        if (queryTrimmed.isEmpty()) {
            return null
        }

        return Pager(
                config = pagingConfig,
                pagingSourceFactory = { SearchPhotosPagingSource(photosRepository, photoSource, queryTrimmed) }
        ).liveData
    }
}