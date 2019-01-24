package com.github.sikv.photos.data

import android.arch.paging.DataSource
import com.github.sikv.photos.api.UnsplashClient
import com.github.sikv.photos.model.Photo

class RecentPhotosDataSourceFactory(
        private val unsplashClient: UnsplashClient

) : DataSource.Factory<Int, Photo>() {

    override fun create(): DataSource<Int, Photo> {
        return RecentPhotosDataSource(unsplashClient)
    }
}