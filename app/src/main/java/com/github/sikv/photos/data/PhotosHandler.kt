package com.github.sikv.photos.data

import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.model.SearchPhotosResponse
import retrofit2.Call

class PhotosHandler(private val photosService: PhotosService) {

    fun getLatestPhotos(page: Int, perPage: Int): Call<List<Photo>> =
            photosService.getPhotos(page, perPage, "latest")

    fun searchPhotos(query: String, page: Int, perPage: Int): RetrofitLiveData<SearchPhotosResponse> =
            RetrofitLiveData(photosService.searchPhotos(query, page, perPage))
}