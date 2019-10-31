package com.github.sikv.photos.api

import com.github.sikv.photos.model.PexelsCuratedPhotosResponse
import com.github.sikv.photos.model.PexelsPhoto
import com.github.sikv.photos.model.PexelsSearchResponse
import retrofit2.Call
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PexelsClient @Inject constructor(private val pexelsApi: PexelsApi) {

    fun getCuratedPhotos(page: Int, perPage: Int): Call<PexelsCuratedPhotosResponse> =
            pexelsApi.getCuratedPhotos(page, perPage)

    fun searchPhotos(query: String, page: Int, perPage: Int): Call<PexelsSearchResponse> =
            pexelsApi.searchPhotos(query, page, perPage)

    fun getPhoto(id: String): Call<PexelsPhoto> =
            pexelsApi.getPhoto(id)
}