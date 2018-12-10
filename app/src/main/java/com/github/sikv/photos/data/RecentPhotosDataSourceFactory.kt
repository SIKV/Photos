package com.github.sikv.photos.data

import android.arch.paging.DataSource
import com.github.sikv.photos.api.UnsplashClient
import com.github.sikv.photos.model.UnsplashPhoto

class RecentPhotosDataSourceFactory(
        private val unsplashClient: UnsplashClient

) : DataSource.Factory<Int, UnsplashPhoto>() {

    override fun create(): DataSource<Int, UnsplashPhoto> {
        return RecentPhotosDataSource(unsplashClient)
    }
}