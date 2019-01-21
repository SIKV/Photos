package com.github.sikv.photos.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.github.sikv.photos.api.ApiClient
import com.github.sikv.photos.data.SearchPhotosDataSource
import com.github.sikv.photos.data.SearchPhotosDataSourceFactory
import com.github.sikv.photos.data.SearchSource
import com.github.sikv.photos.data.State
import com.github.sikv.photos.model.Photo
import java.util.concurrent.Executors

class SearchViewModel : ViewModel() {

    companion object {
        const val INITIAL_LOAD_SIZE = 10
        const val PAGE_SIZE = 10
    }

    private val pagedListConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(INITIAL_LOAD_SIZE)
            .setPageSize(PAGE_SIZE)
            .build()

    private var unsplashSearchDataSourceFactory: SearchPhotosDataSourceFactory? = null
    private var pexelsSearchDataSourceFactory: SearchPhotosDataSourceFactory? = null

    private var unsplashSearchLivePagedList: LiveData<PagedList<Photo>>? = null
    private var pexelsSearchLivePagedList: LiveData<PagedList<Photo>>? = null

    fun getSearchState(searchSource: SearchSource): LiveData<State>? {
        when (searchSource) {
            SearchSource.UNSPLASH -> {
                unsplashSearchDataSourceFactory?.let {
                    return Transformations.switchMap<SearchPhotosDataSource, State>(
                            it.searchDataSourceLiveData, SearchPhotosDataSource::state)
                } ?: run {
                    return null
                }
            }

            SearchSource.PEXELS -> {
                pexelsSearchDataSourceFactory?.let {
                    return Transformations.switchMap<SearchPhotosDataSource, State>(
                            it.searchDataSourceLiveData, SearchPhotosDataSource::state)
                } ?: run {
                    return null
                }
            }
        }
    }

    fun searchPhotos(searchSource: SearchSource, query: String): LiveData<PagedList<Photo>>? {
        val queryTrimmed = query.trim()

        if (queryTrimmed.isEmpty()) {
            return null
        }

        when (searchSource) {
            SearchSource.UNSPLASH -> {
                unsplashSearchDataSourceFactory = SearchPhotosDataSourceFactory(
                        ApiClient.INSTANCE, SearchSource.UNSPLASH, queryTrimmed)

                unsplashSearchLivePagedList = LivePagedListBuilder(unsplashSearchDataSourceFactory!!, pagedListConfig)
                        .setFetchExecutor(Executors.newSingleThreadExecutor())
                        .build()

                return unsplashSearchLivePagedList
            }

            SearchSource.PEXELS -> {
                pexelsSearchDataSourceFactory = SearchPhotosDataSourceFactory(
                        ApiClient.INSTANCE, SearchSource.PEXELS, queryTrimmed)

                pexelsSearchLivePagedList = LivePagedListBuilder(pexelsSearchDataSourceFactory!!, pagedListConfig)
                        .setFetchExecutor(Executors.newSingleThreadExecutor())
                        .build()

                return pexelsSearchLivePagedList
            }
        }
    }

    fun searchListIsEmpty(searchSource: SearchSource): Boolean {
        return when (searchSource) {
            SearchSource.UNSPLASH -> {
                unsplashSearchLivePagedList?.value?.isEmpty() ?: true
            }

            SearchSource.PEXELS -> {
                pexelsSearchLivePagedList?.value?.isEmpty() ?: true
            }
        }
    }
}