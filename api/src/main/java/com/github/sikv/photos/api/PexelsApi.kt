package com.github.sikv.photos.api

import com.github.sikv.photos.domain.pexels.PexelsCuratedPhotosResponse
import com.github.sikv.photos.domain.pexels.PexelsPhoto
import com.github.sikv.photos.domain.pexels.PexelsSearchResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PexelsApi {

    @GET("curated")
    suspend fun getCuratedPhotos(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): PexelsCuratedPhotosResponse

    @GET("search")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): PexelsSearchResponse

    @GET("photos/{id}")
    suspend fun getPhoto(@Path("id") id: String): PexelsPhoto
}
