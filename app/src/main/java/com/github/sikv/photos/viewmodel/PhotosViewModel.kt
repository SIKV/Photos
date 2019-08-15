package com.github.sikv.photos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.github.sikv.photos.api.ApiClient
import com.github.sikv.photos.data.DataSourceState
import com.github.sikv.photos.data.PhotoSource
import com.github.sikv.photos.data.PhotosDataSource
import com.github.sikv.photos.data.PhotosDataSourceFactory
import com.github.sikv.photos.model.Photo
import java.util.concurrent.Executors

class PhotosViewModel : ViewModel() {

    companion object {
        const val INITIAL_LOAD_SIZE = 10
        const val PAGE_SIZE = 10
    }

    private val pagedListConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(INITIAL_LOAD_SIZE)
            .setPageSize(PAGE_SIZE)
            .build()

    private var unsplashDataSourceFactory: PhotosDataSourceFactory? = null
    private var pexelsDataSourceFactory: PhotosDataSourceFactory? = null

    private var unsplashSearchLivePagedList: LiveData<PagedList<Photo>>? = null
    private var pexelsSearchLivePagedList: LiveData<PagedList<Photo>>? = null

    fun getState(photoSource: PhotoSource): LiveData<DataSourceState>? {
        when (photoSource) {
            PhotoSource.UNSPLASH -> {
                unsplashDataSourceFactory?.let {
                    return Transformations.switchMap<PhotosDataSource, DataSourceState>(
                            it.recentPhotosDataSourceLiveData, PhotosDataSource::state)
                } ?: run {
                    return null
                }
            }

            PhotoSource.PEXELS -> {
                pexelsDataSourceFactory?.let {
                    return Transformations.switchMap<PhotosDataSource, DataSourceState>(
                            it.recentPhotosDataSourceLiveData, PhotosDataSource::state)
                } ?: run {
                    return null
                }
            }
        }
    }

    fun getPhotos(photoSource: PhotoSource): LiveData<PagedList<Photo>>? {
        when (photoSource) {
            PhotoSource.UNSPLASH -> {
                unsplashDataSourceFactory = PhotosDataSourceFactory(ApiClient.INSTANCE, PhotoSource.UNSPLASH)

                unsplashSearchLivePagedList = LivePagedListBuilder(unsplashDataSourceFactory!!, pagedListConfig)
                        .setFetchExecutor(Executors.newSingleThreadExecutor())
                        .build()

                return unsplashSearchLivePagedList
            }

            PhotoSource.PEXELS -> {
                pexelsDataSourceFactory = PhotosDataSourceFactory(ApiClient.INSTANCE, PhotoSource.PEXELS)

                pexelsSearchLivePagedList = LivePagedListBuilder(pexelsDataSourceFactory!!, pagedListConfig)
                        .setFetchExecutor(Executors.newSingleThreadExecutor())
                        .build()

                return pexelsSearchLivePagedList
            }
        }
    }
}