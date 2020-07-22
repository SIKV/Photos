package com.github.sikv.photos.api

import com.github.sikv.photos.model.unsplash.UnsplashPhoto
import com.github.sikv.photos.model.unsplash.UnsplashSearchResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UnsplashApi {

    @GET("photos/{id}")
    suspend fun getPhoto(@Path("id") id: String): UnsplashPhoto

    @GET("search/photos")
    suspend fun searchPhotos(@Query("query") query: String, @Query("page") page: Int, @Query("per_page") perPage: Int): UnsplashSearchResponse
}