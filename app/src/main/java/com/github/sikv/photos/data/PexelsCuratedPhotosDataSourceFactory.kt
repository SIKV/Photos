package com.github.sikv.photos.data

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.github.sikv.photos.api.ApiClient
import com.github.sikv.photos.api.PexelsClient
import com.github.sikv.photos.model.Photo

class PexelsCuratedPhotosDataSourceFactory(private val apiClient: PexelsClient) : DataSource.Factory<Int, Photo>() {

    val photosDataSourceLiveData = MutableLiveData<PexelsCuratedPhotosDataSource>()

    override fun create(): DataSource<Int, Photo> {
        val recentPhotosDataSource = PexelsCuratedPhotosDataSource(apiClient)
        photosDataSourceLiveData.postValue(recentPhotosDataSource)

        return recentPhotosDataSource
    }
}