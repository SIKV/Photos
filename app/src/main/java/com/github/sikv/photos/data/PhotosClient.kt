package com.github.sikv.photos.data

import com.github.sikv.photos.api.PhotosApi
import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.model.SearchPhotosResponse
import retrofit2.Call

class PhotosClient(private val photosApi: PhotosApi) {

    fun getLatestPhotos(page: Int, perPage: Int): Call<List<Photo>> =
            photosApi.getPhotos(page, perPage, "latest")

    fun searchPhotos(query: String, page: Int, perPage: Int): Call<SearchPhotosResponse> =
            photosApi.searchPhotos(query, page, perPage)
}