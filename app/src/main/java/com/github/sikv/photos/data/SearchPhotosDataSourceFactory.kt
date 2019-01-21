package com.github.sikv.photos.data

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import com.github.sikv.photos.api.ApiClient
import com.github.sikv.photos.model.Photo

class SearchPhotosDataSourceFactory(
        private val apiClient: ApiClient,
        private val searchSource: SearchSource,
        private val searchQuery: String

) : DataSource.Factory<Int, Photo>() {

    val searchDataSourceLiveData = MutableLiveData<SearchPhotosDataSource>()

    override fun create(): DataSource<Int, Photo> {
        val searchPhotosDataSource = SearchPhotosDataSource(apiClient, searchSource, searchQuery)
        searchDataSourceLiveData.postValue(searchPhotosDataSource)

        return searchPhotosDataSource
    }
}