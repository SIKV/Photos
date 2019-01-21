package com.github.sikv.photos.api

import com.github.sikv.photos.BuildConfig
import com.github.sikv.photos.model.PexelsSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface PexelsApi {

    @Headers("Authorization: ${BuildConfig.PEXELS_API_KEY}")
    @GET("search")
    fun searchPhotos(@Query("query") query: String, @Query("page") page: Int, @Query("per_page") perPage: Int): Call<PexelsSearchResponse>
}