package com.github.sikv.photos.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.github.sikv.photos.api.ApiClient
import com.github.sikv.photos.data.RecentPhotosDataSourceFactory
import com.github.sikv.photos.model.Photo
import java.util.concurrent.Executors

class MainViewModel : ViewModel() {

    companion object {

        const val INITIAL_LOAD_SIZE = 10
        const val PAGE_SIZE = 10
    }


    var recentPhotos: LiveData<PagedList<Photo>>


    init {
        val recentPhotosDataSource = RecentPhotosDataSourceFactory(ApiClient.INSTANCE.photosClient)

        val pagedListConfig = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(INITIAL_LOAD_SIZE)
                .setPageSize(PAGE_SIZE)
                .build()

        recentPhotos = LivePagedListBuilder<Int, Photo>(recentPhotosDataSource, pagedListConfig)
                .setFetchExecutor(Executors.newSingleThreadExecutor())
                .build()
    }

    fun searchPhotos(query: String) {

    }
}