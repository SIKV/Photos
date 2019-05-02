package com.github.sikv.photos.data

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import com.github.sikv.photos.api.UnsplashClient
import com.github.sikv.photos.model.Photo

class RecentPhotosDataSourceFactory(
        private val unsplashClient: UnsplashClient

) : DataSource.Factory<Int, Photo>() {

    val recentPhotosDataSourceLiveData = MutableLiveData<RecentPhotosDataSource>()

    override fun create(): DataSource<Int, Photo> {
        val recentPhotosDataSource = RecentPhotosDataSource(unsplashClient)
        recentPhotosDataSourceLiveData.postValue(recentPhotosDataSource)

        return recentPhotosDataSource
    }
}