package com.github.sikv.photos.api

import com.github.sikv.photos.model.UnsplashPhoto
import com.github.sikv.photos.model.UnsplashSearchResponse
import retrofit2.Call

class UnsplashClient(private val unsplashApi: UnsplashApi) {

    fun getLatestPhotos(page: Int, perPage: Int): Call<List<UnsplashPhoto>> =
            unsplashApi.getPhotos(page, perPage, "latest")

    fun searchPhotos(query: String, page: Int, perPage: Int): Call<UnsplashSearchResponse> =
            unsplashApi.searchPhotos(query, page, perPage)
}