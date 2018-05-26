package com.github.sikv.photos.viewmodel

import android.arch.lifecycle.ViewModel
import com.github.sikv.photos.data.DataHandler

class PhotosViewModel : ViewModel() {

    val photos = DataHandler.INSTANCE.photosHandler.geLatestPhotos(1, 10)

    override fun onCleared() {
        photos.cancel()
    }
}