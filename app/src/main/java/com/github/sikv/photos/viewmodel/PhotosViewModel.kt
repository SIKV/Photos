package com.github.sikv.photos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.github.sikv.photos.App
import com.github.sikv.photos.api.ApiClient
import com.github.sikv.photos.data.PexelsCuratedPhotosDataSource
import com.github.sikv.photos.data.PexelsCuratedPhotosDataSourceFactory
import com.github.sikv.photos.data.repository.FavoritesRepository
import com.github.sikv.photos.enumeration.DataSourceState
import com.github.sikv.photos.enumeration.PhotoSource
import com.github.sikv.photos.event.Event
import com.github.sikv.photos.event.VoidEvent
import com.github.sikv.photos.model.Photo
import java.util.concurrent.Executors
import javax.inject.Inject

class PhotosViewModel : ViewModel(), FavoritesRepository.Listener {

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

    private var pexelsDataSourceFactoryPexelsCurated: PexelsCuratedPhotosDataSourceFactory? = null
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

    fun getLoadingState(): LiveData<DataSourceState>? {
        pexelsDataSourceFactoryPexelsCurated?.let {
            return Transformations.switchMap<PexelsCuratedPhotosDataSource, DataSourceState>(
                    it.photosDataSourceLiveData, PexelsCuratedPhotosDataSource::state)
        } ?: run {
            return null
        }
    }

    fun getPexelsCuratedPhotos(): LiveData<PagedList<Photo>>? {
        pexelsDataSourceFactoryPexelsCurated = PexelsCuratedPhotosDataSourceFactory(ApiClient.INSTANCE.pexelsClient)

        pexelsSearchLivePagedList = LivePagedListBuilder(pexelsDataSourceFactoryPexelsCurated!!, pagedListConfig)
                .setFetchExecutor(Executors.newSingleThreadExecutor())
                .build()

        return pexelsSearchLivePagedList
    }
}