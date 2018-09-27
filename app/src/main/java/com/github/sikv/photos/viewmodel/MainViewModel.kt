package com.github.sikv.photos.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.github.sikv.photos.api.ApiClient
import com.github.sikv.photos.data.RecentPhotosDataSourceFactory
import com.github.sikv.photos.data.SearchPhotosDataSource
import com.github.sikv.photos.data.SearchPhotosDataSourceFactory
import com.github.sikv.photos.data.State
import com.github.sikv.photos.model.Photo
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

    var recentPhotos: LiveData<PagedList<Photo>>

    private var searchDataSourceFactory: SearchPhotosDataSourceFactory? = null
    private var searchLivePagedList: LiveData<PagedList<Photo>>? = null

    init {
        val recentPhotosDataSource = RecentPhotosDataSourceFactory(ApiClient.INSTANCE.photosClient)

        recentPhotos = LivePagedListBuilder<Int, Photo>(recentPhotosDataSource, pagedListConfig)
                .setFetchExecutor(Executors.newSingleThreadExecutor())
                .build()
    }

    fun getSearchState(): LiveData<State>? {
        searchDataSourceFactory?.let {
            return Transformations.switchMap<SearchPhotosDataSource, State>(
                    it.searchDataSourceLiveData, SearchPhotosDataSource::state)

        } ?: run {
            return null
        }
    }

    fun searchPhotos(query: String): LiveData<PagedList<Photo>>? {
        val queryTrimmed = query.trim()

        if (queryTrimmed.isEmpty()) {
            return null
        }

        searchDataSourceFactory = SearchPhotosDataSourceFactory(ApiClient.INSTANCE.photosClient, queryTrimmed)

        searchLivePagedList = LivePagedListBuilder(searchDataSourceFactory!!, pagedListConfig)
                .setFetchExecutor(Executors.newSingleThreadExecutor())
                .build()

        return searchLivePagedList
    }

    fun searchListIsEmpty(): Boolean {
        return searchLivePagedList?.value?.isEmpty() ?: true
    }
}