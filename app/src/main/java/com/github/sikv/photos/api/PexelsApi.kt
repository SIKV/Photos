package com.github.sikv.photos.api

import com.github.sikv.photos.model.PexelsCuratedPhotosResponse
import com.github.sikv.photos.model.PexelsPhoto
import com.github.sikv.photos.model.PexelsSearchResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PexelsApi {

    @GET("curated")
    fun getCuratedPhotos(@Query("page") page: Int, @Query("per_page") perPage: Int): Single<PexelsCuratedPhotosResponse>

    @GET("search")
    fun searchPhotos(@Query("query") query: String, @Query("page") page: Int, @Query("per_page") perPage: Int): Single<PexelsSearchResponse>

    @GET("photos/{id}")
    fun getPhoto(@Path("id") id: String): Single<PexelsPhoto>
}