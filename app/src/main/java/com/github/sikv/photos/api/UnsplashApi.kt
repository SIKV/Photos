package com.github.sikv.photos.api

import com.github.sikv.photos.model.unsplash.UnsplashPhoto
import com.github.sikv.photos.model.unsplash.UnsplashSearchResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UnsplashApi {

    @GET("photos/{id}")
    fun getPhoto(@Path("id") id: String): Single<UnsplashPhoto>

    @GET("search/photos")
    fun searchPhotos(@Query("query") query: String, @Query("page") page: Int, @Query("per_page") perPage: Int): Single<UnsplashSearchResponse>
}