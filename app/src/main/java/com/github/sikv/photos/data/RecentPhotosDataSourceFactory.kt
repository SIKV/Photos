package com.github.sikv.photos.data

import android.arch.paging.DataSource
import com.github.sikv.photos.model.Photo

class RecentPhotosDataSourceFactory(
        private val photosClient: PhotosClient

) : DataSource.Factory<Int, Photo>() {

    override fun create(): DataSource<Int, Photo> {
        return RecentPhotosDataSource(photosClient)
    }
}