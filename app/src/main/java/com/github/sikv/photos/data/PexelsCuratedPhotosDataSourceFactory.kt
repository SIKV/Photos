package com.github.sikv.photos.data

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.github.sikv.photos.model.Photo

class PexelsCuratedPhotosDataSourceFactory : DataSource.Factory<Int, Photo>() {

    val photosDataSourceLiveData = MutableLiveData<PexelsCuratedPhotosDataSource>()

    override fun create(): DataSource<Int, Photo> {
        val recentPhotosDataSource = PexelsCuratedPhotosDataSource()
        photosDataSourceLiveData.postValue(recentPhotosDataSource)

        return recentPhotosDataSource
    }
}