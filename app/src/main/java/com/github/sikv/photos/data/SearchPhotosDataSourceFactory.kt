package com.github.sikv.photos.data

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.github.sikv.photos.api.ApiClient
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.enumeration.PhotoSource

class SearchPhotosDataSourceFactory(
        private val apiClient: ApiClient,
        private val photoSource: PhotoSource,
        private val searchQuery: String

) : DataSource.Factory<Int, Photo>() {

    val searchDataSourceLiveData = MutableLiveData<SearchPhotosDataSource>()

    override fun create(): DataSource<Int, Photo> {
        val searchPhotosDataSource = SearchPhotosDataSource(apiClient, photoSource, searchQuery)
        searchDataSourceLiveData.postValue(searchPhotosDataSource)

        return searchPhotosDataSource
    }
}