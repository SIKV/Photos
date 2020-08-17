package com.github.sikv.photos.api

import com.github.sikv.photos.model.pixabay.PixabayPhoto
import com.github.sikv.photos.model.pixabay.PixabaySearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface PixabayApi {

    @GET(".")
    suspend fun searchPhotos(@Query("q") query: String,
                             @Query("page") page: Int,
                             @Query("per_page") perPage: Int): PixabaySearchResponse

    @GET(".")
    suspend fun getPhoto(@Query("id") id: String): PixabayPhoto
}