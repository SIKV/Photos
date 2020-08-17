package com.github.sikv.photos.data

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.github.sikv.photos.enumeration.PhotoSource
import com.github.sikv.photos.model.Photo

class SearchPhotosDataSourceFactory(
        private val photoSource: PhotoSource,
        private val searchQuery: String
) : DataSource.Factory<Int, Photo>() {

    val searchDataSourceLiveData = MutableLiveData<SearchPhotosDataSource>()

    override fun create(): DataSource<Int, Photo> {
        val searchPhotosDataSource = SearchPhotosDataSource(photoSource, searchQuery)
        searchDataSourceLiveData.postValue(searchPhotosDataSource)

        return searchPhotosDataSource
    }
}