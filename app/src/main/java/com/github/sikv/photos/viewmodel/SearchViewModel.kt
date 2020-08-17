package com.github.sikv.photos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.github.sikv.photos.App
import com.github.sikv.photos.data.SearchPhotosDataSource
import com.github.sikv.photos.data.SearchPhotosDataSourceFactory
import com.github.sikv.photos.data.repository.FavoritesRepository
import com.github.sikv.photos.enumeration.DataSourceState
import com.github.sikv.photos.enumeration.PhotoSource
import com.github.sikv.photos.event.Event
import com.github.sikv.photos.event.VoidEvent
import com.github.sikv.photos.model.Photo
import java.util.concurrent.Executors
import javax.inject.Inject

class SearchViewModel : ViewModel(), FavoritesRepository.Listener {

    companion object {
        const val INITIAL_LOAD_SIZE = 10
        const val PAGE_SIZE = 10
    }

    @Inject
    lateinit var favoritesRepository: FavoritesRepository

    private val pagedListConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(INITIAL_LOAD_SIZE)
            .setPageSize(PAGE_SIZE)
            .build()

    private val dataSourceFactories = mutableMapOf<PhotoSource, SearchPhotosDataSourceFactory>()
    private val livePagedLists = mutableMapOf<PhotoSource, LiveData<PagedList<Photo>>?>()

    private val favoriteChangedMutableEvent = MutableLiveData<Event<Photo>>()
    val favoriteChangedEvent: LiveData<Event<Photo>> = favoriteChangedMutableEvent

    private val favoritesChangedMutableEvent = MutableLiveData<VoidEvent>()
    val favoritesChangedEvent: LiveData<VoidEvent> = favoritesChangedMutableEvent

    init {
        App.instance.appComponent.inject(this)

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

    fun getSearchLoadingState(photoSource: PhotoSource): LiveData<DataSourceState>? {
        dataSourceFactories[photoSource]?.let {
            return Transformations.switchMap<SearchPhotosDataSource, DataSourceState>(
                    it.searchDataSourceLiveData, SearchPhotosDataSource::state)
        } ?: run {
            return null
        }
    }

    fun searchPhotos(photoSource: PhotoSource, query: String): LiveData<PagedList<Photo>>? {
        val queryTrimmed = query.trim()

        if (queryTrimmed.isEmpty()) {
            return null
        }

        val dataSourceFactory = SearchPhotosDataSourceFactory(photoSource, queryTrimmed)
        dataSourceFactories[photoSource] = dataSourceFactory

        livePagedLists[photoSource] = LivePagedListBuilder(dataSourceFactory, pagedListConfig)
                .setFetchExecutor(Executors.newSingleThreadExecutor())
                .build()

        return livePagedLists[photoSource]
    }

    fun isSearchListEmpty(photoSource: PhotoSource): Boolean {
        return livePagedLists[photoSource]?.value?.isEmpty() ?: true
    }
}