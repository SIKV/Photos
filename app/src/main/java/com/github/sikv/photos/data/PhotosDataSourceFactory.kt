package com.github.sikv.photos.data

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import com.github.sikv.photos.api.ApiClient
import com.github.sikv.photos.model.Photo


class PhotosDataSourceFactory(
        private val apiClient: ApiClient,
        private val photoSource: PhotoSource

        ) : DataSource.Factory<Int, Photo>() {

    val recentPhotosDataSourceLiveData = MutableLiveData<PhotosDataSource>()

    override fun create(): DataSource<Int, Photo> {
        val recentPhotosDataSource = PhotosDataSource(apiClient, photoSource)
        recentPhotosDataSourceLiveData.postValue(recentPhotosDataSource)

        return recentPhotosDataSource
    }
}