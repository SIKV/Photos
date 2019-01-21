package com.github.sikv.photos.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.github.sikv.photos.api.ApiClient
import com.github.sikv.photos.data.RecentPhotosDataSourceFactory
import com.github.sikv.photos.model.UnsplashPhoto
import java.util.concurrent.Executors

class MainViewModel : ViewModel() {

    companion object {
        const val INITIAL_LOAD_SIZE = 10
        const val PAGE_SIZE = 10
    }

    private val pagedListConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(INITIAL_LOAD_SIZE)
            .setPageSize(PAGE_SIZE)
            .build()

    var recentPhotos: LiveData<PagedList<UnsplashPhoto>>

    init {
        val recentPhotosDataSource = RecentPhotosDataSourceFactory(ApiClient.INSTANCE.unsplashClient)

        recentPhotos = LivePagedListBuilder<Int, UnsplashPhoto>(recentPhotosDataSource, pagedListConfig)
                .setFetchExecutor(Executors.newSingleThreadExecutor())
                .build()
    }
}