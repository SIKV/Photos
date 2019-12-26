package com.github.sikv.photos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.github.sikv.photos.App
import com.github.sikv.photos.api.ApiClient
import com.github.sikv.photos.data.DataSourceState
import com.github.sikv.photos.data.PhotoSource
import com.github.sikv.photos.data.SearchPhotosDataSource
import com.github.sikv.photos.data.SearchPhotosDataSourceFactory
import com.github.sikv.photos.data.FavoritesRepository
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.util.Event
import com.github.sikv.photos.util.VoidEvent
import java.util.concurrent.Executors
import javax.inject.Inject

class SearchViewModel : ViewModel(), FavoritesRepository.Callback {

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

    private var unsplashSearchDataSourceFactory: SearchPhotosDataSourceFactory? = null
    private var pexelsSearchDataSourceFactory: SearchPhotosDataSourceFactory? = null

    private var unsplashSearchLivePagedList: LiveData<PagedList<Photo>>? = null
    private var pexelsSearchLivePagedList: LiveData<PagedList<Photo>>? = null

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
        when (photoSource) {
            PhotoSource.UNSPLASH -> {
                unsplashSearchDataSourceFactory?.let {
                    return Transformations.switchMap<SearchPhotosDataSource, DataSourceState>(
                            it.searchDataSourceLiveData, SearchPhotosDataSource::state)
                } ?: run {
                    return null
                }
            }

            PhotoSource.PEXELS -> {
                pexelsSearchDataSourceFactory?.let {
                    return Transformations.switchMap<SearchPhotosDataSource, DataSourceState>(
                            it.searchDataSourceLiveData, SearchPhotosDataSource::state)
                } ?: run {
                    return null
                }
            }
        }
    }

    fun searchPhotos(photoSource: PhotoSource, query: String): LiveData<PagedList<Photo>>? {
        val queryTrimmed = query.trim()

        if (queryTrimmed.isEmpty()) {
            return null
        }

        when (photoSource) {
            PhotoSource.UNSPLASH -> {
                unsplashSearchDataSourceFactory = SearchPhotosDataSourceFactory(
                        ApiClient.INSTANCE, PhotoSource.UNSPLASH, queryTrimmed)

                unsplashSearchLivePagedList = LivePagedListBuilder(unsplashSearchDataSourceFactory!!, pagedListConfig)
                        .setFetchExecutor(Executors.newSingleThreadExecutor())
                        .build()

                return unsplashSearchLivePagedList
            }

            PhotoSource.PEXELS -> {
                pexelsSearchDataSourceFactory = SearchPhotosDataSourceFactory(
                        ApiClient.INSTANCE, PhotoSource.PEXELS, queryTrimmed)

                pexelsSearchLivePagedList = LivePagedListBuilder(pexelsSearchDataSourceFactory!!, pagedListConfig)
                        .setFetchExecutor(Executors.newSingleThreadExecutor())
                        .build()

                return pexelsSearchLivePagedList
            }
        }
    }

    fun isSearchListEmpty(photoSource: PhotoSource): Boolean {
        return when (photoSource) {
            PhotoSource.UNSPLASH -> {
                unsplashSearchLivePagedList?.value?.isEmpty() ?: true
            }

            PhotoSource.PEXELS -> {
                pexelsSearchLivePagedList?.value?.isEmpty() ?: true
            }
        }
    }
}