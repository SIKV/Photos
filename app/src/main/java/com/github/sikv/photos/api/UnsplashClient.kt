package com.github.sikv.photos.api

import com.github.sikv.photos.model.Photo
import com.github.sikv.photos.model.SearchPhotosResponse
import retrofit2.Call

class UnsplashClient(private val unsplashApi: UnsplashApi) {

    fun getLatestPhotos(page: Int, perPage: Int): Call<List<Photo>> =
            unsplashApi.getPhotos(page, perPage, "latest")

    fun searchPhotos(query: String, page: Int, perPage: Int): Call<SearchPhotosResponse> =
            unsplashApi.searchPhotos(query, page, perPage)
}