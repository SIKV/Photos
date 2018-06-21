package com.github.sikv.photos.data

import android.arch.paging.DataSource
import com.github.sikv.photos.model.Photo

class RecentPhotosDataSourceFactory(
        private val photosStorage: PhotosHandler

) : DataSource.Factory<Int, Photo>() {

    override fun create(): DataSource<Int, Photo> {
        return RecentPhotosDataSource(photosStorage)
    }
}