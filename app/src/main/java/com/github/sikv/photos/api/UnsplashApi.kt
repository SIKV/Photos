package com.github.sikv.photos.api

import com.github.sikv.photos.model.UnsplashPhoto
import com.github.sikv.photos.model.UnsplashSearchResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UnsplashApi {

    @GET("photos")
    fun getPhotos(@Query("page") page: Int, @Query("per_page") perPage: Int, @Query("order_by") orderBy: String): Single<List<UnsplashPhoto>>

    @GET("photos/{id}")
    fun getPhoto(@Path("id") id: String): Single<UnsplashPhoto>

    @GET("search/photos")
    fun searchPhotos(@Query("query") query: String, @Query("page") page: Int, @Query("per_page") perPage: Int): Single<UnsplashSearchResponse>
}