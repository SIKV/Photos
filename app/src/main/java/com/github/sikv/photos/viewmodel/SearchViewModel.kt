package com.github.sikv.photos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.liveData
import com.github.sikv.photos.config.ConfigProvider
import com.github.sikv.photos.data.repository.FavoritesRepository
import com.github.sikv.photos.data.repository.PhotosRepository
import com.github.sikv.photos.data.source.SearchPhotosPagingSource
import com.github.sikv.photos.event.Event
import com.github.sikv.photos.event.VoidEvent
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.model.PhotoSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val photosRepository: PhotosRepository,
    private val favoritesRepository: FavoritesRepository,
    private val configProvider: ConfigProvider
) : ViewModel(), FavoritesRepository.Listener {

    private val mutableSearchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String> = mutableSearchQuery

    private val mutableFavoriteToggled = MutableLiveData<Event<Photo>>()
    val favoriteToggled: LiveData<Event<Photo>> = mutableFavoriteToggled

    private val mutableFavoriteListToggled = MutableLiveData<VoidEvent>()
    val favoriteListToggled: LiveData<VoidEvent> = mutableFavoriteListToggled

    init {
        favoritesRepository.subscribe(this)
    }

    override fun onCleared() {
        super.onCleared()

        favoritesRepository.unsubscribe(this)
    }

    override fun onFavoriteChanged(photo: Photo, isFavorite: Boolean) {
        mutableFavoriteToggled.postValue(Event(photo))
    }

    override fun onFavoritesChanged() {
        mutableFavoriteListToggled.postValue(VoidEvent())
    }

    fun requestSearch(text: String) {
        mutableSearchQuery.postValue(text)
    }

    fun searchPhotos(photoSource: PhotoSource, query: String): LiveData<PagingData<Photo>>? {
        val queryTrimmed = query.trim()

        if (queryTrimmed.isEmpty()) {
            return null
        }

        return Pager(
            config = configProvider.getPagingConfig(),
            pagingSourceFactory = {
                SearchPhotosPagingSource(photosRepository, photoSource, queryTrimmed)
            }
        ).liveData
    }

    fun toggleFavorite(photo: Photo) {
        favoritesRepository.invertFavorite(photo)
    }
}
