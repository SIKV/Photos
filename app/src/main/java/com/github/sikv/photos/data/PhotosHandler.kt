package com.github.sikv.photos.data

import com.github.sikv.photos.model.Photo

class PhotosHandler(private val photosService: PhotosService) {

    fun geLatestPhotos(page: Int, perPage: Int): RetrofitLiveData<List<Photo>> =
            RetrofitLiveData(photosService.getPhotos(page, perPage, "latest"))
}