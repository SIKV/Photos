package com.github.sikv.photos.data

import android.arch.paging.DataSource
import com.github.sikv.photos.api.PhotosClient
import com.github.sikv.photos.model.Photo

class SearchPhotosDataSourceFactory(
        private val photosClient: PhotosClient,
        private val searchQuery: String

) : DataSource.Factory<Int, Photo>() {

    override fun create(): DataSource<Int, Photo> {
        return SearchPhotosDataSource(photosClient, searchQuery)
    }
}