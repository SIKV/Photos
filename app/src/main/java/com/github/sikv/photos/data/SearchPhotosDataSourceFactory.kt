package com.github.sikv.photos.data

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import com.github.sikv.photos.api.UnsplashClient
import com.github.sikv.photos.model.Photo

class SearchPhotosDataSourceFactory(
        private val unsplashClient: UnsplashClient,
        private val searchQuery: String

) : DataSource.Factory<Int, Photo>() {

    val searchDataSourceLiveData = MutableLiveData<SearchPhotosDataSource>()

    override fun create(): DataSource<Int, Photo> {
        val searchPhotosDataSource = SearchPhotosDataSource(unsplashClient, searchQuery)
        searchDataSourceLiveData.postValue(searchPhotosDataSource)

        return searchPhotosDataSource
    }
}